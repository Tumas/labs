#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>

#include "protocol.h"
#include "server/server.h"

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
  char *s_port = "8000";
  char *c_port = "8001";
  spellcast_server* srv;
  server_meta srv_info = {
    (stream_meta*) malloc(sizeof(stream_meta)),
    "You are connected to a dummy spellcast server.",
    16000
  };

  srv_info.server_data->name = "SpellCast dummy server";
  srv_info.server_data->genre = "unknown"; 
  srv_info.server_data->url =  "http://github.com/Tumas/labs/V_semester/networks";
  srv_info.server_data->mime_type = "audio/mpeg";
  srv_info.server_data->bitrate = 0;
  srv_info.server_data->pub = 0;

  while ((opt = getopt(argc, argv, "s:c:n:n:u:pm:")) != -1){
    switch(opt){
      case 's':
        s_port = optarg;
        break;
      case 'c':
        c_port = optarg;
        break;
      case 'n':
        srv_info.server_data->name = optarg;
        break;
      case 't':
        srv_info.notice = optarg;
        break;
      case 'u':
        srv_info.server_data->url = optarg;
        break;
      case 'p':
        srv_info.server_data->pub = atoi(optarg);
        break;
      case 'm':
        srv_info.metaint = atoi(optarg);
        break;
      case '?':
        fprintf(stdout, "Unrecognized option (will have no effect) : %c\n", optopt);
        break;
    }
  }

  if (!strcmp(c_port, s_port)){
    P_ERROR("You must set different ports\n");
    free(srv_info.server_data);
  }

  srv = spellcast_init_server_variables(s_port, c_port, &srv_info);

  fprintf(stdout, " *** Starting Server: *** \n\n");
  spellcast_print_server_info(srv);
  fprintf(stdout, " ***  *** \n\n");

  if (!spellcast_init_server(srv)){
    spellcast_server_run(srv);
  }

  spellcast_server_dispose(srv);
  free(srv_info.server_data);
  return 0;
}
