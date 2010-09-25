#include <arpa/inet.h>
#include "client.h"

int
spellcast_accept_client(spellcast_server* srv)
{
  struct sockaddr_storage remote_addr;
  socklen_t addrlen = sizeof remote_addr;
  client_meta *new_cl;
  int new_cl_sock;

  char remote_ip[INET6_ADDRSTRLEN];

  if (srv->connected_clients == MAX_CLIENTS){
    P_ERROR("max number of connected clients already reached");
    return -1;
  }

  /*
  if (srv->connected_sources == 0){
    P_ERROR("There are no sources that client could connect to");
    return -1;
  }
  */

  if ((new_cl_sock = accept(srv->cl_sock, (struct sockaddr*) &remote_addr, &addrlen)) == -1){
    perror("Client accept");
    return -1;
  }

  fprintf(stdout, "New client connection from: %s on socket %d\n",
      (const char*) inet_ntop(remote_addr.ss_family, get_in_addr((struct sockaddr*)&remote_addr), remote_ip, INET6_ADDRSTRLEN),
      new_cl_sock);

  if (new_cl_sock > srv->latest_sock){
    srv->latest_sock = new_cl_sock;
  }

  new_cl = spellcast_create_empty_client(new_cl_sock);
  if (new_cl){
    FD_SET(new_cl_sock, &srv->master_read);
    spellcast_add_empty_client(srv, new_cl);

    // We need to parse information from a client first
    //  to know if it supports metadata 
    //   and only then register it with corresponing source
    FD_SET(new_cl_sock, &srv->empty_clients);
  }
  else {
    close(new_cl_sock);
  }

  return 0;
}

client_meta* 
spellcast_create_empty_client(int socket)
{
  client_meta *new_client = (client_meta*) malloc(sizeof(client_meta));

  if (new_client == NULL){
    P_ERROR("malloc gave null: spawning new client_meta");
    return NULL;
  }

  new_client->name = NULL;
  new_client->sock_d = socket;
  new_client->buf_start = 0;
  new_client->metaint = 0;

  return new_client;
}

void
spellcast_register_client(spellcast_server *srv, client_meta *client)
{
  source_meta* src = spellcast_get_source_by_mountpoint(srv, client->mountpoint);
  int i;

  if (src){
    for (i = 0; i < MAX_CLIENTS; i++){
      if (src->clients[i] == NULL){
        src->clients[i] = client;
        src->connected_clients++;
        break; 
      }
    }
  }
}

client_meta*
spellcast_get_client(spellcast_server *srv, int socket)
{
  int i;
  int connected = srv->connected_clients;

  for (i = 0; i < connected; i++){
    if (srv->clients[i] && srv->clients[i]->sock_d == socket){
      return srv->clients[i];
    }
  }

  return NULL;
}

void 
spellcast_add_empty_client(spellcast_server *srv, client_meta *client)
{
  int i;

  for (i = 0; i < MAX_CLIENTS; i++){
    if (srv->clients[i] == NULL){
      srv->clients[i] = client;
      srv->connected_clients++;
      break;
    }
  }
}

int
spellcast_client_parse_header(spellcast_server *srv, client_meta *client)
{
  int received_bytes = recv(client->sock_d, client->buffer + client->buf_start, CHAR_SOURCE_BUFFER_SIZE(client), 0); 
  if (received_bytes == 0){
    spellcast_disconnect_client(srv, client);

    return 2;
  }

  client->buffer[client->buf_start + received_bytes] = '\0';
                printf("BUFFER: %s\n", client->buffer); 

  char *header_line, *match, *prev_line, *get_token = "GET";
  char *metaint_sep = "Icy-MetaData:";
  char *client_name_sep = "User-Agent:";

  // clean header end
  int is_full_message = strstr(client->buffer, srv->icy_p->source_header->header_end) != NULL ? 1 : 0;

  header_line = strtok(client->buffer, "\r\n");
  while (header_line != NULL){
    //printf("parsed_token: %s\n", header_line);

    if ((match = strstr(header_line, get_token)) != NULL) {
      char *buf = spellcast_allocate_string(header_line);
      int len = strlen(buf);
      char *test = strtok(buf, " ");
      char *prev = NULL;

      while (test != NULL){
        if (prev != NULL && strcmp(prev, get_token) == 0){
          client->mountpoint = spellcast_allocate_string(test + 1);
          break;
        }

        prev = test;
        test = strtok(NULL, " ");
      }

      // restore original string : black magic
      header_line = strtok(header_line + len + 1, "\r\n"); 
      free(buf);

    } else if ((match = strstr(header_line, client_name_sep)) != NULL) {
      client->name = spellcast_allocate_string(match + strlen(client_name_sep));

    } else if ((match = strstr(header_line, metaint_sep)) != NULL) {
      client->metaint = atoi(match + strlen(metaint_sep));
    }

    prev_line = header_line;
    header_line = strtok(NULL, "\r\n"); 
  }
  
  if (!is_full_message){
    client->buf_start = strlen(prev_line);
    strcpy(client->buffer, prev_line);

    return 0;
  }
  else { 
    client->buf_start = 0;

    //TODO : if client sets no mountpoint -> randomly connect to some of the connected sources
  }

  return 1;
}

void
spellcast_dispose_client(client_meta *client)
{
  free(client->mountpoint);
  free(client->name);
  free(client);
}

/**/
void
spellcast_disconnect_client(spellcast_server *srv, client_meta *client)
{
  int i;
  source_meta* source = spellcast_get_source_by_mountpoint(srv, client->mountpoint);

  FD_CLR(client->sock_d, &srv->master_read);

  for (i = 0; i < MAX_CLIENTS; i++){
    if (source->clients[i] && source->clients[i]->sock_d == client->sock_d){
      source->clients[i] = NULL;
      source->connected_clients--;
      break;
    }
  }

  spellcast_dispose_client(client);
}

/**/
void spellcast_print_client_info(client_meta *client)
{
  printf(" **** Client **** \n");
  printf("\tSocket: %d\n", client->sock_d);
  printf("\tName: %s\n", client->name);
  printf("\tMountpoint: %s\n", client->mountpoint);
  printf("\tCan handle metadata: %d\n", client->metaint);
  printf(" **** END **** \n");
}
