#include <stdio.h>
#include <stdlib.h>
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

  srv->client_port = 8000;
  srv->source_port = srv->client_port + 1;

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

static void
init_server(spellcast_server *srv)
{
}

static void 
dispose_server(spellcast_server *srv)
{
  free(srv->server_metadata->server_data);
  free(srv->server_metadata);
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
