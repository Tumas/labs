#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>

#include "../protocol.h"
#include "server.h"

/* Command line arguments :
 *  -s port - source port
 *  -c port - client port
 *  -n name - name of a server
 *  -t notice - notice message sent to connected clients
 *  -m metaint - interval at which metadata is sent to clients
 *  -u url     - server's url
 *  -p int     - flag that determines if server is public (1, 0 otherwise)
 */

int 
main(int argc, char *argv[])
{
  int opt;

  spellcast_server srv;
  srv.meta = (stream_meta*) malloc(sizeof(stream_meta));
  srv.notice = "You are connected to a dummy spellcast server.";
  srv.source_port = "8001";
  srv.client_port = "8000";

  srv.meta->name = "SpellCast dummy server";
  srv.meta->genre = "unknown"; 
  srv.meta->url =  "http://github.com/Tumas/labs/V_semester/networks";
  srv.meta->mime_type = "audio/mpeg";
  srv.meta->bitrate = 0;
  srv.meta->pub = 0;

  while ((opt = getopt(argc, argv, "s:c:n:t:u:p:m:")) != -1){
    switch(opt){
      case 's':
        srv.source_port = optarg;
        break;
      case 'c':
        srv.client_port = optarg;
        break;
      case 'n':
        srv.meta->name = optarg;
        break;
      case 't':
        srv.notice = optarg;
        break;
      case 'u':
        srv.meta->url = optarg;
        break;
      case 'p':
        srv.meta->pub =  atoi(optarg) ? 1 : 0;
        break;
      case 'm':
        srv.metaint = (unsigned int) atoi(optarg);
        break;
      case '?':
        fprintf(stdout, "Unrecognized option (will have no effect) : %c\n", optopt);
        break;
    }
  }

  if (!strcmp(srv.source_port, srv.client_port)){
    P_ERROR("You must set different ports\n");
    free(srv.meta);
    return -1;
  }

  fprintf(stdout, " *** Starting Server: *** \n\n");
  spellcast_print_server_info(&srv);
  fprintf(stdout, " ***  *** \n\n");

  if (!spellcast_init_server(&srv)){
    spellcast_server_run(&srv);
  }

  spellcast_server_dispose(&srv);
  free(srv.meta);
  return 0;
}
