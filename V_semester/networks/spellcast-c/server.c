#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>

#include "spellcast.h"

int
main(int argc, char *argv[])
{
  spellcast_server* server_info;
  //TODO: argument checking

  server_info = init_variables(argc, argv);
  if (server_info == NULL){
    dispose_server(server_info);
    return 1;
  }

  init_server(server_info);

  dispose_server(server_info);
  return 0;
}

static spellcast_server*
init_variables(int args, char *argv[])
{
  spellcast_server *srv;
  srv = (spellcast_server*) malloc(sizeof(spellcast_server));
  
  if (!srv){
    P_ERROR("malloc gave NULL (intializing server)");
    return NULL; 
  }

  srv->client_port = "8000";
  srv->source_port = "8001";

  // server metadata
  srv->server_metadata = (server_meta*) malloc(sizeof(server_meta));
  if (!srv->server_metadata){
    P_ERROR("malloc gave NULL (initializing server : server metadata)");
    return NULL;
  }

  srv->server_metadata->metaint = 16000;
  srv->server_metadata->notice = "You are connected to a dummy spellcast server 0.1";

  srv->server_metadata->server_data = (stream_meta*) malloc(sizeof(stream_meta));
  if (!srv->server_metadata->server_data){
    P_ERROR("malloc gave NULL (initializing server : stream data)");
    return NULL;
  }

  srv->server_metadata->server_data->name = "SpellCast dummy server";
  srv->server_metadata->server_data->url = "http://github.com/Tumas/labs/V_semester/networks";
  srv->server_metadata->server_data->mime_type = "audio/mpeg";
  srv->server_metadata->server_data->pub = 0; /* private server */

  return srv;
}


static int
init_server(spellcast_server *srv)
{
  memset(&srv->hints, 0, sizeof srv->hints);
  srv->hints.ai_family = AF_UNSPEC;
  srv->hints.ai_socktype = SOCK_STREAM;
  srv->hints.ai_flags = AI_PASSIVE;

  srv->src_sock = create_access_point(&srv->hints, &srv->srv_src_addrinfo, srv->source_port);
  srv->cl_sock = create_access_point(&srv->hints, &srv->srv_cl_addrinfo, srv->client_port);

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

  FD_ZERO(&srv->read_socks);
  
  return 0;
}

int
create_access_point(struct addrinfo* hints, struct addrinfo** serv_info, char *port)
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

static void 
dispose_server(spellcast_server *srv)
{
  free(srv->server_metadata->server_data);
  free(srv->server_metadata);

  freeaddrinfo(srv->srv_src_addrinfo);
  freeaddrinfo(srv->srv_cl_addrinfo);
  free(srv);
}


/*
static void 
run(server_meta *srv)
{
}

static void 
accept_source(server_meta *srv)
{
}

static void 
accept_client()
{
}

static void
broadcast()
{
}
*/
