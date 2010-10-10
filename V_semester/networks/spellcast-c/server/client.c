#include <arpa/inet.h>
#include "client.h"
#include "source.h"

int
spellcast_accept_client(spellcast_server* srv)
{
  struct sockaddr_storage remote_addr;
  socklen_t addrlen = sizeof remote_addr;
  client_meta *new_cl;
  int new_cl_sock;

  char remote_ip[INET6_ADDRSTRLEN];

  if ((new_cl_sock = accept(srv->cl_sock, (struct sockaddr*) &remote_addr, &addrlen)) == -1){
    perror("Client accept");
    return -1;
  }

  if (srv->connected_clients == MAX_CLIENTS){
    P_ERROR("max number of connected clients already reached");
    close(new_cl_sock);
    return -1;
  }

  if (srv->connected_sources == 0){
  P_ERROR("There are no sources that client could connect to");
    close(new_cl_sock);
    return -1;
  }

  fprintf(stdout, "New client connection from: %s on socket %d\n",
      (const char*) inet_ntop(remote_addr.ss_family, get_in_addr((struct sockaddr*)&remote_addr), remote_ip, INET6_ADDRSTRLEN),
      new_cl_sock);

  if (new_cl_sock > srv->latest_sock){
    srv->latest_sock = new_cl_sock;
  }

  new_cl = spellcast_allocate_space_for_empty_client(new_cl_sock);
  if (new_cl){
    new_cl->c_point->sock_d = new_cl_sock;
    FD_SET(new_cl_sock, &srv->master_read);
    FD_SET(new_cl_sock, &srv->empty_clients);

    /* spellcast_add_empty_client(srv, new_cl); */
    spellcast_htable_insert(srv->clients, new_cl_sock, (void*) new_cl);
    srv->connected_clients++;
    
    /* 
      We need to parse information from a client first
      to know if it supports metadata 
      and only then register it with corresponing source
    */
    
  }
  else {
    close(new_cl_sock);
  }

  return 0;
}

client_meta*
spellcast_allocate_space_for_empty_client(int sock)
{
  client_meta *cl =  (client_meta*) malloc(sizeof(client_meta));
  cl->c_point = (connection_point*) malloc(sizeof(connection_point));
  memset(cl->c_point, 0, sizeof(connection_point));

  cl->c_point->meta = (stream_meta*) malloc(sizeof(stream_meta));

  if (cl == NULL || cl->c_point == NULL || cl->c_point->meta == NULL){
    P_ERROR("malloc gave null: spawning new client meta");

    free(cl->c_point->meta);
    free(cl->c_point);
    free(cl);
    return NULL;
  }

  memset(cl->c_point->meta, 0, sizeof(stream_meta));
  cl->metaint = 0; 
  cl->bytes_to_meta = 0;

  return cl;
}

// TODO: add third parameter to disconnect client
void 
spellcast_disconnect_client(spellcast_server *srv, client_meta *client)
{
  void *data;
  close(client->c_point->sock_d);

  source_meta *src = spellcast_get_source_by_mountpoint(srv, client->c_point->mountpoint);
  dlist_remove_by_data(src->clients, (void*) client, &data);

  spellcast_htable_remove(srv->clients, client->c_point->sock_d, &data);
  FD_CLR(client->c_point->sock_d, &srv->master_read);

  spellcast_dispose_client((void*)client);
  srv->connected_clients--;
  src->connected_clients--;

  spellcast_print_server_stats(srv);
}

void
spellcast_dispose_client(void *cl)
{
  client_meta* client = (client_meta*) cl;

  spellcast_dispose_stream_meta(client->c_point->meta);
  free(client->c_point->meta);
  free(client->c_point);
  free(client);
}

void
spellcast_print_client_info(client_meta *client)
{
  printf(" **** Client **** \n");
  printf("\tSocket: %d\n", client->c_point->sock_d);
  printf("\tName: %s\n", client->c_point->meta->name);
  printf("\tMountpoint: %s\n", client->c_point->mountpoint);
  printf("\tCan handle metadata: %d\n", client->metaint);
  printf(" **** END **** \n");
}


void 
spellcast_client_header_parse_callback(char *str, void *client)
{
  if ((strstr(str, SPELLCAST_METAINT_TOKEN)) != NULL) {
    ((client_meta*) client)->metaint = atoi(str + strlen(SPELLCAST_METAINT_TOKEN));
  }
}

