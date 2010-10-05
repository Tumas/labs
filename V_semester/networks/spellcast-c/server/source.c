#include <arpa/inet.h>
#include "source.h"
#include "client.h"

int
spellcast_accept_source(spellcast_server* srv)
{
  struct sockaddr_storage remote_addr;
  socklen_t addrlen = sizeof remote_addr;
  source_meta *new_src;
  int new_src_sock;
  char remote_ip[INET6_ADDRSTRLEN];

  if (srv->connected_sources == MAX_SOURCES){
    P_ERROR("max number of connected sources already reached");
    return -1;
  }

  if ((new_src_sock = accept(srv->src_sock, (struct sockaddr*) &remote_addr, &addrlen)) == -1){
    perror("Source accept");
    return -1;
  }

  fprintf(stdout, "New connection from: %s on socket %d\n",
      (const char*) inet_ntop(remote_addr.ss_family, get_in_addr((struct sockaddr*)&remote_addr), remote_ip, INET6_ADDRSTRLEN),
      new_src_sock);

  if (new_src_sock > srv->latest_sock){
    srv->latest_sock = new_src_sock;
  }

  new_src = spellcast_allocate_space_for_empty_source(new_src_sock);

  if (new_src){
    printf("SUCESUFULU ALLOCATED NEW SOURCE\n");
    spellcast_register_source(srv, new_src);
    FD_SET(new_src_sock, &srv->master_read);
    FD_SET(new_src_sock, &srv->empty_sources);
  }
  else {
    close(new_src_sock);
  }

  return 0;
}

source_meta* 
spellcast_allocate_space_for_empty_source(int sock)
{
  source_meta* source = (source_meta*) malloc(sizeof(source_meta));
  source->c_point = (connection_point*) malloc(sizeof(connection_point));
  memset(source->c_point, 0, sizeof(connection_point));
  source->c_point->meta = (stream_meta*) malloc(sizeof(stream_meta));

  if (source == NULL || source->c_point == NULL || source->c_point->meta == NULL){
    P_ERROR("malloc gave null: spawning new source_meta");

    free(source->c_point->meta);
    free(source->c_point);
    free(source);
    return NULL;
  }

  memset(source->c_point->meta, 0, sizeof(stream_meta));
  source->connected_clients = 0;
  source->c_point->sock_d = sock;

  source->clients = (DList*) malloc(sizeof(DList));
  dlist_init(source->clients, spellcast_dispose_client, NULL);

  return source;
}

int 
spellcast_register_source(spellcast_server *srv, source_meta *source)
{
  int status; 
  srv->connected_sources++;
  printf("%p %p %d\n", source, source->c_point, source->c_point->sock_d);
  if ((status = spellcast_htable_insert(srv->sources, source->c_point->sock_d, (void*) source)) != 0){
    printf("Could not insert source into HT with key %d\n", source->c_point->sock_d);
  }

  return status;
}

void
spellcast_dispose_source(void *src)
{
  source_meta *source = (source_meta*) src;
  dlist_destroy(source->clients);
  free(source->clients);

  spellcast_dispose_stream_meta(source->c_point->meta);
  free(source->c_point->meta);
  free(source->c_point);
  free(source);
}

void 
spellcast_print_source_info(source_meta *source)
{
  printf(" **** SOURCE **** \n");
  printf("\tName: %s\n", source->c_point->meta->name);
  printf("\tDesc: %s\n", source->c_point->description);
  printf("\tURL: %s\n", source->c_point->meta->url);
  printf("\tPublic: %d\n", source->c_point->meta->pub);
  printf("\tBitrate: %dkbps\n", source->c_point->meta->bitrate);
  printf("\tMount point: %s\n", source->c_point->mountpoint);
  printf("\tConnected clients: %d\n", source->connected_clients);
}

source_meta* 
spellcast_get_source_by_mountpoint(spellcast_server *srv, char* mnt)
{
  source_meta *source, *obj = NULL;
  hash_iterator *iter = spellcast_create_hash_iterator(srv->sources);

  while ((source = spellcast_hash_iterator_next(iter)) != NULL){
    if (strcmp(mnt, ((source_meta*) source)->c_point->mountpoint) == 0){
      obj = source;
      break;
    }
  }

  free(iter);
  return obj;
}

source_meta*
spellcast_get_random_source(spellcast_server *srv)
{
  int s_num = rand() % srv->connected_sources + 1;
  int sum = 0;
  hash_iterator *iter = spellcast_create_hash_iterator(srv->sources);
  source_meta *obj;

  while (sum != s_num){
    obj = (source_meta*) spellcast_hash_iterator_next(iter);
    sum++;
  }

  free(iter);
  return obj;
}

void 
spellcast_disconnect_source(spellcast_server *srv, source_meta *source)
{
  void *data;
  listElement *client = dlist_head(source->clients);
  listElement *temp;
  
  printf("DISCONNECTING SOURCE: \n");
  spellcast_print_source_info(source);

  printf("DISCONNECTING ITS CLIENTS: \n");
  while (client != NULL) {
    temp = dlist_next(client);

    //TODO: we could just pass source to client disconnection : wrong way
    spellcast_print_client_info(dlist_data(client));
    spellcast_disconnect_client(srv, dlist_data(client));
    client = temp;
  };

  spellcast_htable_remove(srv->sources, source->c_point->sock_d, &data);
  FD_CLR(source->c_point->sock_d, &srv->master_read);
  
  spellcast_dispose_source(source);

  srv->connected_sources--;
  spellcast_print_server_stats(srv);
}
