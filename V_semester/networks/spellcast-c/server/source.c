#include "source.h"

int
spellcast_accept_source(spellcast_server* srv)
{
  struct sockaddr_storage remote_addr;
  socklen_t addrlen = sizeof remote_addr;
  source_meta *new_src;
  int new_src_sock, len;

  char remote_ip[INET6_ADDRSTRLEN];

  if (srv->connected_sources == MAX_SOURCES){
    P_ERROR("max number of clients already reached");
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

  new_src = spellcast_create_empty_source(new_src_sock);

  if (new_src){
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
spellcast_create_empty_source(int sock)
{
  source_meta* source = (source_meta*) malloc(sizeof(source_meta));
  if (source == NULL){
    P_ERROR("malloc gave null: spawning new source_meta");
    return NULL;
  }

  memset(source->buffer, 0, sizeof(source->buffer));
  source->sock_d = sock;

  source->stream_data = (stream_meta*) malloc(sizeof(stream_meta));
  if (source->stream_data == NULL){
    free(source);

    P_ERROR("malloc gave null: spawning new source_meta");
    return NULL;
  }

  memset(source->stream_data, 0, sizeof(stream_meta));
  return source;
}

void 
spellcast_register_source(spellcast_server *srv, source_meta *source)
{
  int i;
  int connected = ++(srv->connected_sources);

  for (i = 0; i < connected; i++){
    if (srv->sources[i] == NULL){
      srv->sources[i] = source;
      break;
    }
  }
}

source_meta* 
spellcast_get_source(spellcast_server *srv, int socket)
{
  int i;
  int connected = srv->connected_sources;

  for (i = 0; i < connected; i++){
    if (srv->sources[i] && srv->sources[i]->sock_d == socket){
      return srv->sources[i];
    }
  }

  return NULL;
}

void
spellcast_dispose_source(source_meta *source)
{
  spellcast_dispose_stream_meta(source->stream_data);
  free(source);
}

void 
spellcast_disconnect_source(spellcast_server *srv, source_meta *source)
{
  int i, connected = srv->connected_sources;

  FD_CLR(source->sock_d, &srv->master_read);
  for (i = 0; i < connected; i++){
    if (srv->sources[i] && srv->sources[i]->sock_d == source->sock_d){
      srv->sources[i] = NULL;
      srv->connected_sources--;
      break;
    }
  }

  // TODO: disconnect clients
  spellcast_dispose_source(source);
}
