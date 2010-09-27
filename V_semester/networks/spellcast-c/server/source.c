#include <arpa/inet.h>
#include "source.h"

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
  source->buf_start = 0;

  source->stream_data = (stream_meta*) malloc(sizeof(stream_meta));
  if (source->stream_data == NULL){
    free(source);

    P_ERROR("malloc gave null: spawning new source_meta");
    return NULL;
  }

  memset(source->stream_data, 0, sizeof(stream_meta));
  memset(source->clients, 0, MAX_CLIENTS * sizeof(client_meta*));
  return source;
}

void 
spellcast_register_source(spellcast_server *srv, source_meta *source)
{
  int i;

  for (i = 0; i < MAX_SOURCES; i++){
    if (srv->sources[i] == NULL){
      srv->sources[i] = source;
      srv->connected_sources++;
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

source_meta* 
spellcast_get_source_by_mountpoint(spellcast_server *srv, char* mnt)
{
  int i;
  int connected = srv->connected_sources;

  for (i = 0; i < connected; i++){
    if (srv->sources[i] && strcmp(srv->sources[i]->mountpoint, mnt) == 0){
      return srv->sources[i];
    }
  }

  return NULL;
}

void
spellcast_dispose_source(source_meta *source)
{
  spellcast_dispose_stream_meta(source->stream_data);
  free(source->description);
  free(source->user_agent);
  free(source);
}

void 
spellcast_disconnect_source(spellcast_server *srv, source_meta *source)
{
  printf("DISCONNECTING SOURCE: \n");
  spellcast_print_source_info(source);

  int i;

  FD_CLR(source->sock_d, &srv->master_read);
  for (i = 0; i < MAX_SOURCES; i++){
    if (srv->sources[i] && srv->sources[i]->sock_d == source->sock_d){
      srv->sources[i] = NULL;
      srv->connected_sources--;
      break;
    }
  }

  // TODO: disconnect clients
  spellcast_dispose_source(source);
  spellcast_print_server_stats(srv);
}

int
spellcast_source_parse_header(spellcast_server *srv, source_meta *source)
{
  memset(source->buffer + source->buf_start, 0, SOURCE_BUFFER_SIZE(source));
  
  int received_bytes = recv(source->sock_d, source->buffer + source->buf_start, CHAR_SOURCE_BUFFER_SIZE(source), 0); 
  if (received_bytes == 0){
    spellcast_disconnect_source(srv, source);

    return 2;
  }

  source->buffer[source->buf_start + received_bytes] = '\0';

  char *header_line, *match, *prev_line;
  int is_full_message = strstr(source->buffer, srv->icy_p->source_header->header_end) != NULL ? 1 : 0;

  header_line = strtok(source->buffer, "\r\n");
  while (header_line != NULL){
    //printf("parsed_token: %s\n", header_line);

    if ((match = strstr(header_line, srv->icy_p->source_header->source_sep)) != NULL) {
      char *buf = spellcast_allocate_string(header_line);
      int len = strlen(buf);
      char *test = strtok(buf, " ");
      char *prev = NULL;

      while (test != NULL){
        if (prev != NULL && strcmp(prev, srv->icy_p->source_header->source_sep) == 0){
          source->mountpoint = spellcast_allocate_string(test + 1);
          break;
        }

        prev = test;
        test = strtok(NULL, " ");
      }

      // restore original string : black magic
      header_line = strtok(header_line + len + 1, "\r\n"); 
      free(buf);

    } else if ((match = strstr(header_line, srv->icy_p->source_header->url_sep)) != NULL) {
      source->stream_data->url = spellcast_allocate_string(match + strlen(srv->icy_p->source_header->url_sep));

    } else if ((match = strstr(header_line, srv->icy_p->source_header->description_sep)) != NULL) {
      source->description = spellcast_allocate_string(match + strlen(srv->icy_p->source_header->description_sep));

    } else if ((match = strstr(header_line, srv->icy_p->source_header->user_agent_sep)) != NULL) {
      source->user_agent = spellcast_allocate_string(match + strlen(srv->icy_p->source_header->user_agent_sep));

    } else if ((match = strstr(header_line, srv->icy_p->source_header->public_sep)) != NULL) {
      source->stream_data->pub = atoi(match + strlen(srv->icy_p->source_header->public_sep));

    } else if ((match = strstr(header_line, srv->icy_p->source_header->bitrate_sep)) != NULL) {
      source->stream_data->bitrate = atoi(match + strlen(srv->icy_p->source_header->bitrate_sep));
    }

    prev_line = header_line;
    header_line = strtok(NULL, "\r\n"); 
  }
  
  if (!is_full_message){
    source->buf_start = strlen(prev_line);
    strcpy(source->buffer, prev_line);

    return 0;
  }
  else { 
    source->buf_start = 0;

    if (strlen(source->mountpoint) == 0 || spellcast_source_mountpoint_taken(srv, source)){
      char mnt[10];
      sprintf(mnt, "mount%d", source->sock_d);
      source->mountpoint = spellcast_allocate_string(mnt);
    }
  }

  return 1;
}

int
spellcast_source_mountpoint_taken(spellcast_server* srv, source_meta* source)
{
  int i;

  for (i = 0; i < srv->connected_sources; i++){
    if (source != srv->sources[i] && strcmp(source->mountpoint, srv->sources[i]->mountpoint) == 0){
      return 1;
    }
  }

  return 0;
}

void 
spellcast_print_source_info(source_meta *source)
{
  printf(" **** SOURCE **** \n");
  printf("\tName: %s\n", source->stream_data->name);
  printf("\tDesc: %s\n", source->description);
  printf("\tUser-Agent: %s\n", source->user_agent);
  printf("\tURL: %s\n", source->stream_data->url);
  printf("\tPublic: %d\n", source->stream_data->pub);
  printf("\tBitrate: %dkbps\n", source->stream_data->bitrate);
  printf("\tMount point: %s\n", source->mountpoint);
}
