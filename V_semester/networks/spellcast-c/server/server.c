#include <time.h>
#include "server.h"
#include "source.h"
#include "client.h"

void
spellcast_print_server_info(const spellcast_server* srv)
{
  fprintf(stdout, "Server: %s\nURL: %s\nServing: %s\n", srv->meta->name,
      srv->meta->url, srv->meta->mime_type);
  fprintf(stdout, "Source port: %s\n", srv->source_port);
  fprintf(stdout, "Client port: %s\n", srv->client_port);
}

void
spellcast_print_server_stats(const spellcast_server *srv)
{
  fprintf(stdout, "Connected sources %d\n", srv->connected_sources);
  fprintf(stdout, "Connected clients %d\n", srv->connected_clients);
}

static int 
spellcast_create_access_point(struct addrinfo* hints, struct addrinfo** serv_info, char *port)
{
  int status, sock, reuse_flag = 1;
  struct addrinfo *temp;

  if ((status = getaddrinfo(NULL, port, hints, serv_info)) != 0){
    P_ERROR(gai_strerror(status));
    return -1;
  }

  /* 
    Now, it's possible to get some addresses that don't work for one reason or another, so what the Linux man page does is loops through the list doing a call to socket() and connect() (or bind() if you're setting up a server with the AI_PASSIVE flag) until it succeeds.
   */
  for (temp = *serv_info; temp != NULL; temp = temp->ai_next){
    // create a socket
    sock = socket(temp->ai_family, temp->ai_socktype, temp->ai_protocol);
    if (sock < 0){ 
      continue;
    }

    // set reuseaddr
    setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &reuse_flag, sizeof(int));

    // bind socket
    if (bind(sock, temp->ai_addr, temp->ai_addrlen) < 0){
      close(sock);
      continue;
    }

    return sock;
  }

  return -1; //could not bind
}

int 
source_socket_match(int key, const void *src2)
{
  return key == ((source_meta*)src2)->c_point->sock_d;
}

int 
client_socket_match(int key, const void *cl)
{
  return key == ((client_meta*)cl)->c_point->sock_d;
}

int
spellcast_init_server(spellcast_server *srv)
{
  srand(time(NULL));

  memset(&srv->hints, 0, sizeof(srv->hints));
  srv->hints.ai_family = AF_UNSPEC;
  srv->hints.ai_socktype = SOCK_STREAM;
  srv->hints.ai_flags = AI_PASSIVE;

  srv->src_sock = spellcast_create_access_point(&srv->hints, &srv->srv_src_addrinfo, srv->source_port);
  srv->cl_sock = spellcast_create_access_point(&srv->hints, &srv->srv_cl_addrinfo, srv->client_port);

  if (srv->src_sock == -1 || srv->cl_sock == -1){
    close(srv->src_sock);
    close(srv->cl_sock);

    P_ERROR("Server could not bind");
    return -1;
  }

  if (listen(srv->src_sock, SOURCEBACKLOG) == -1 || listen(srv->cl_sock, CLIENTBACKLOG) == -1){
    perror("Could not listen");
    return -1;
  }

  FD_ZERO(&srv->master_read);
  FD_SET(srv->src_sock, &srv->master_read);
  FD_SET(srv->cl_sock, &srv->master_read);
  srv->latest_sock = srv->cl_sock;

  srv->connected_clients = 0;
  srv->connected_sources = 0;

  srv->clients = (sp_hash_table*) malloc(sizeof(sp_hash_table));
  srv->sources = (sp_hash_table*) malloc(sizeof(sp_hash_table));

  spellcast_htable_init(srv->clients, HASHSIZE, client_socket_match, spellcast_dispose_client);
  spellcast_htable_init(srv->sources, HASHSIZE, source_socket_match, spellcast_dispose_source);

  return 0;
}

int 
spellcast_server_run(spellcast_server *srv)
{
  int i, temp1;
  int received_bytes, sent_bytes;
  fd_set temp_read;
  source_meta *source;
  client_meta *client;

  FD_ZERO(&temp_read);

  while (1){
    temp_read = srv->master_read;
    if (select(srv->latest_sock+1, &temp_read, NULL, NULL, NULL) == -1){
      perror("Select");
      return -1;
    }

    for(i = 0; i <= srv->latest_sock; i++){
      if (FD_ISSET(i, &temp_read)){
        
        if (i == srv->src_sock){
          spellcast_accept_source(srv);
          spellcast_print_server_stats(srv);
        }
        else if (i == srv->cl_sock){
          spellcast_accept_client(srv);
          spellcast_print_server_stats(srv);
        }
        else {
           spellcast_htable_lookup(srv->sources, i, (void **) &source);

          if (source){
            if (FD_ISSET(i, &srv->empty_sources)){
              if (spellcast_parse_header(srv, source->c_point, NULL, NULL)){
                spellcast_print_source_info(source);
                spellcast_print_server_stats(srv);

                send_message(source->c_point->sock_d, SPELLCAST_SRV2SRC_OK_MSG, strlen(SPELLCAST_SRV2SRC_OK_MSG));

                FD_CLR(i, &srv->empty_sources);
              }
            }
            else {
              // replace with actual data
              icy_metadata stub = { NULL, "U2 - One" };

              if ((received_bytes = recv(source->c_point->sock_d, source->c_point->buffer, BUFFLEN, 0)) == 0){
                spellcast_disconnect_source(srv, source);
              }
              else 
              {
                // send data to clients
                listElement* element = dlist_head(source->clients);
                
                while (element != NULL){
                  client = (client_meta*) dlist_data(element);

                  if (client->bytes_to_meta + received_bytes > METAINT){
                    temp1 = send_message(client->c_point->sock_d, source->c_point->buffer, METAINT - client->bytes_to_meta);

                    if (spellcast_send_metadata(client->c_point->sock_d, &stub) == -1){
                      spellcast_disconnect_client(srv, client);

                      element = dlist_next(element);
                      continue;
                    }

                    // send rest of bytes + increase ->bytes_to_meta
                    client->bytes_to_meta = send_message(client->c_point->sock_d, source->c_point->buffer + METAINT - client->bytes_to_meta, (client->bytes_to_meta + received_bytes) % METAINT);
                    sent_bytes = temp1 + client->bytes_to_meta;
                  }
                  else {
                    sent_bytes = send_message(client->c_point->sock_d, source->c_point->buffer, received_bytes);
                    client->bytes_to_meta += sent_bytes;
                  }

                  if (sent_bytes != received_bytes){
                    spellcast_disconnect_client(srv, client);
                    
                    element = dlist_next(element);
                    continue;
                  }

                  element = dlist_next(element);
                } // while 
              }
            }
          }
          else {
            spellcast_htable_lookup(srv->clients, i, (void **) &client);
              printf("LOOK UP RESULT FOR CLIENT %p\n", client);

            if (client){
              if (FD_ISSET(i, &srv->empty_clients)){
                printf("EMPYCLIENT\n");

                // will block if client sends metadata without header_end at all
                if (spellcast_parse_header(srv, client->c_point, client, spellcast_client_header_parse_callback)){
                  spellcast_print_client_info(client);
                  spellcast_print_server_stats(srv);

                   source_meta* src = NULL;
                   if (client->c_point->mountpoint != NULL) {
                     src = spellcast_get_source_by_mountpoint(srv, client->c_point->mountpoint);
                   }

                   // send info about source
                   if (src == NULL){
                     if (srv->connected_sources > 0){
                       // could not source with given mountpoint 
                       printf("Could not find source with specified mountpoint : %s\n", client->c_point->mountpoint);
                       printf("Connecting to a random source...\n");

                       src = spellcast_get_random_source(srv);
                       printf("Random source found\n");

                       free(client->c_point->mountpoint);
                       printf("removed old mountpoint\n");
                       client->c_point->mountpoint = spellcast_allocate_string(src->c_point->mountpoint);
                       printf("Random source selected: %p (%s) \n", src, client->c_point->mountpoint);
                     }
                     else {
                       printf(" --> should not happen <-- ");
                     }
                   }

                   // register with source
                   dlist_insert(src->clients, (const void*) client);

                   char mes[BUFFLEN];
                   sprintf(mes, ICY_SRV2CLIENT_MESSAGE, srv->notice,
                        src->c_point->meta->name,
                        src->c_point->meta->genre,
                        src->c_point->meta->url, 
                        srv->meta->mime_type,
                        src->c_point->meta->pub,
                        src->c_point->meta->bitrate, 
                        METAINT);

                   send_message(client->c_point->sock_d, mes, strlen(mes));

                   FD_CLR(i, &srv->empty_clients);
                }
              }
              else {
                  received_bytes = recv(client->c_point->sock_d, client->c_point->buffer, BUFFLEN, 0);
                  if (received_bytes == 0){
                    printf(" *** CLIENT on socket %d disconnected ***\n", i);
                    spellcast_disconnect_client(srv, client);
                  }
              }
            }
          }
        }
      } // FD_ISSET main
    }
  } // while(1)

  return 0;
}

void* 
get_in_addr(struct sockaddr *sa)
{
  if (sa->sa_family == AF_INET){
    return &(((struct sockaddr_in*)sa)->sin_addr);
  }

  return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

void 
spellcast_server_dispose(spellcast_server *srv)
{
  close(srv->src_sock);
  close(srv->cl_sock);

  freeaddrinfo(srv->srv_src_addrinfo);
  freeaddrinfo(srv->srv_cl_addrinfo);
  freeaddrinfo(&srv->hints);

  spellcast_htable_destroy(srv->clients);
  spellcast_htable_destroy(srv->sources);
  free(srv->sources);
  free(srv->clients);
}

void 
spellcast_dispose_stream_meta(stream_meta *stream)
{
  free(stream->name);
  free(stream->genre);
  free(stream->url);
  free(stream->mime_type);
}


// TODO : normalize this with returns and stuff for disconnection
int 
spellcast_parse_header(spellcast_server *srv, connection_point* cnp, void* object, void (*callback)(char *string, void *obj))
{
  int received_bytes, is_full_message; 
  char *header_line, *match, *prev_line;
  
  memset(cnp->buffer + cnp->buf_start, 0, CPOINT_BUFFER_SIZE(cnp));

  received_bytes = recv(cnp->sock_d, cnp->buffer + cnp->buf_start, CHAR_CPOINT_BUFFER_SIZE(cnp), 0); 
  if (received_bytes == 0){
    //spellcast_disconnect_source(srv, source);
    return -1;
  }

  printf("GOT: \n%s\n", cnp->buffer);
  cnp->buffer[cnp->buf_start + received_bytes] = '\0';
  is_full_message = strstr(cnp->buffer, SPELLCAST_HEADER_END_TOKEN) != NULL ? 1 : 0;
  header_line = strtok(cnp->buffer, SPELLCAST_LINE_TOKEN);


  while (header_line != NULL){
    str_to_lower(header_line);
    //printf("parsed_token: %s\n", header_line);

    // This could be moved to a function : expecting client or source?
    if ((match = strstr(header_line, SPELLCAST_SOURCE_TOKEN)) != NULL || (match = strstr(header_line, SPELLCAST_CLIENT_TOKEN)) != NULL) {
      char *buf = spellcast_allocate_string(header_line);
      int len = strlen(buf);
      char *test = strtok(buf, " ");
      char *prev = NULL;

      while (test != NULL){
        if (prev != NULL && (strcmp(prev, SPELLCAST_SOURCE_TOKEN) == 0 || strcmp(prev, SPELLCAST_CLIENT_TOKEN) == 0)){
          if (strlen(test) > 1){
            cnp->mountpoint = spellcast_allocate_string(test + 1);
            printf("MOUNTPOINT allocated: %s\n", cnp->mountpoint);
          }
          else {
            printf("MOUNTPOINT not set. Setting to null\n");
          }
          break;
        }

        prev = test;
        test = strtok(NULL, " ");
      }

      // restore original string : black magic
      header_line = strtok(header_line + len + 1, SPELLCAST_LINE_TOKEN); 
      free(buf);

    } else if ((match = strstr(header_line, SPELLCAST_URL_TOKEN)) != NULL) {
      cnp->meta->url = spellcast_allocate_string(match + strlen(SPELLCAST_URL_TOKEN));

    } else if ((match = strstr(header_line, SPELLCAST_DESCRIPTION_TOKEN)) != NULL) {
      cnp->description = spellcast_allocate_string(match + strlen(SPELLCAST_DESCRIPTION_TOKEN));

    } else if ((match = strstr(header_line, SPELLCAST_USER_AGENT_TOKEN)) != NULL) {
      cnp->meta->name = spellcast_allocate_string(match + strlen(SPELLCAST_USER_AGENT_TOKEN));

    } else if ((match = strstr(header_line, SPELLCAST_PUBLIC_TOKEN)) != NULL) {
      cnp->meta->pub = atoi(match + strlen(SPELLCAST_PUBLIC_TOKEN));

    } else if ((match = strstr(header_line, SPELLCAST_BITRATE_TOKEN)) != NULL) {
      cnp->meta->bitrate = atoi(match + strlen(SPELLCAST_BITRATE_TOKEN));
    }

    if (callback)
      callback(header_line, object);

    prev_line = header_line;
    header_line = strtok(NULL, "\r\n"); 
  }
  
  if (!is_full_message){
    cnp->buf_start = strlen(prev_line);
    strcpy(cnp->buffer, prev_line);

    return 0;
  }
  else { 
    cnp->buf_start = 0;

    if (cnp->mountpoint){
      // What if mountpoint is aready taken?
      source_meta *test = spellcast_get_source_by_mountpoint(srv, cnp->mountpoint);
      if (test && test->c_point != cnp){
        char mnt[10];
        sprintf(mnt, "mount%d", cnp->sock_d);
        cnp->mountpoint = spellcast_allocate_string(mnt);
      }
    }
  }

  return 1;
}

int  
spellcast_send_metadata(int socket, icy_metadata *meta)
{
  // 28 is the number of additional characters in metadata without url and title
  int msg_len = ((meta->title != NULL) ? strlen(meta->title) : 0) + ((meta->url != NULL) ? strlen(meta->url) : 0) + 28;
  int padding = 16 - msg_len % 16;
  int to_write = msg_len + padding + 1; 
  char *buf = malloc(to_write);

  memset(buf, 0, to_write);
  sprintf(buf, ICY_METADATA_FORMAT, (msg_len + padding) / 16, meta->title != NULL ? meta->title : "", meta->url != NULL ? meta->url : "");

  msg_len = send_message(socket, buf, to_write);
  free(buf);

  return msg_len == to_write ? 1 : -1;
}
