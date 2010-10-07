#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <string.h>

#include "client.h"
#include "../protocol.h"
#include "../helpers.h"

#define BUFFER_SIZE 8192

/* Command line arguments :
 *  -u url - url to connecto to 
 *  -p port - port
 *  -m mountpoint - mountpoint 
 *  -i    - specify if dont want to get metadata
 *
 *  TODO:
 *    recovery for small buffer
 *    playback
 *    data handling
 *
 *    testing with metadata on and off
 */

int
main(int argc, char *argv[])
{
  int opt, temp;

  char *port = "8000", *url = "127.0.0.1", *m_point = "";
  spellcast_con_info *c_info = (spellcast_con_info*) malloc(sizeof(spellcast_con_info));
  c_info->needs_meta = 1;

  struct addrinfo hints, *servinfo, *p;
  int rv, bytes_received;
  char buffer[BUFFER_SIZE];

  while ((opt = getopt(argc, argv, "u:p:m:hi")) != -1){
    switch(opt){
      case 'p':
        temp = atoi(optarg);

        if (temp < 1024 || temp > 65535) 
          fprintf(stderr, "NOTICE: Wrong port specified. Using default port : %s\n", port);
        else 
          port = optarg;

        break;
      case 'u':
        url = optarg;
        break;
      case 'm':
        m_point = optarg;
        break;
      case 'i':
        c_info->needs_meta = 0;
        break;
      case 'h':
        fprintf(stdout, "USAGE: %s -p port -u url -m mountpoint\n", argv[0]);
        break;
      case '?':
        fprintf(stdout, "Unrecognized option (will have no effect) : %c\n", optopt);
        break;
    }
  }

  printf("Connecting to http://%s:%s/%s ...\n", url, port, m_point);

  memset(&hints, 0, sizeof hints);
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;
 
  if ((rv = getaddrinfo(url, port, &hints, &servinfo)) != 0){
    fprintf(stderr, " getaddrinfo: %s\n", gai_strerror(rv));
    return 1;
  }

  for (p = servinfo; p != NULL; p = p->ai_next){
    if ((c_info->sock_d = socket(p->ai_family, p->ai_socktype, p->ai_protocol)) == -1){
      P_ERROR("Could not create socket\n");
      continue;
    }

    if (connect(c_info->sock_d, p->ai_addr, p->ai_addrlen) == -1){
      close(c_info->sock_d);
      P_ERROR("Could not connect on socket\n");
      continue;
    }

    break;
  }

  if (p == NULL){
    fprintf(stderr, "Client: failed to connect\n");
    return 1;
  }

  freeaddrinfo(servinfo);

  /* Send information about client */
  sprintf(buffer, ICY_CLIENT2SRV_MESSAGE, m_point, "http 1.1", url, port, "TEST agent", 1, "close");
  send_message(c_info->sock_d, buffer, strlen(buffer));

  /* Parse information from server */
  if ((bytes_received = recv(c_info->sock_d, buffer, BUFFER_SIZE - 1, 0)) == 0){
    perror("recv");
    return 1;
  }

  buffer[bytes_received] = '\0';
  printf("SERVER RESPONDED with %d bytes\n\n%s\n", bytes_received, buffer);

  spellcast_client_parse_server_response(buffer, c_info);
  spellcast_client_print_connection_info(c_info);
  
  spellcast_client_play(c_info);

  spellcast_client_cleanup(c_info);
  return 0;
}

void 
spellcast_client_parse_server_response(const char *response, spellcast_con_info *sp)
{
   char rsp[strlen(response) + 1];
   strcpy(rsp, response);

   char *header_line = strtok(rsp, SPELLCAST_LINE_TOKEN);
   char *match;

   while (header_line != NULL){
     str_to_lower(header_line);

     if ((match = strstr(header_line, SPELLCAST_BR_TOKEN)) != NULL){
       sp->bitrate = atoi(match + strlen(SPELLCAST_BR_TOKEN));
     }
     else if ((match = strstr(header_line, SPELLCAST_METAINT_CLIENT_TOKEN)) != NULL){
       sp->metaint = atoi(match + strlen(SPELLCAST_METAINT_CLIENT_TOKEN));
     }

     header_line = strtok(NULL, SPELLCAST_LINE_TOKEN);
   }
}

void 
spellcast_client_print_connection_info(const spellcast_con_info *c_info)
{
  printf("CONNECTION INFO:\n Bitrate: %dkbps\n Metaint:%d\n\n", c_info->bitrate, c_info->metaint);
}

void 
spellcast_client_play(spellcast_con_info *c_info)
{
  char buffer[BUFFER_SIZE];
  char meta_info[255 * 16];

  int bytes_received;
  int bytes_to_meta = 0, meta_length, filled_meta_length, getting_meta = 0;
  char l_byte;


  while ((bytes_received = recv(c_info->sock_d, buffer, BUFFER_SIZE, 0)) > 1){
    //printf("client received: %d\n", bytes_received);
    //printf("Bytes to meta %d\n", bytes_to_meta);
    //printf("getting_meta? %d\n", getting_meta);

    if (!getting_meta) {

      if (bytes_received + bytes_to_meta > c_info->metaint){
        printf("metaint %d\n", c_info->metaint);
        printf("bytes_received %d\n", bytes_received);
        printf("bytes_to_meta %d\n", bytes_to_meta);
        printf("bytes_received + bytes_to_meta %d\n", bytes_received + bytes_to_meta);
        printf("meta starts at %d - %d : %d\n", c_info->metaint, bytes_to_meta, c_info->metaint - bytes_to_meta);

        // music bytes : c_info->metaint - bytes_to_meta
        l_byte = buffer[c_info->metaint - bytes_to_meta];
        meta_length = buffer[c_info->metaint - bytes_to_meta] * 16;
        printf("metalen %d\n", meta_length);

        /*
        printf("l_byte char %c\n", (int) l_byte);
        printf("l_byte char atoi %d\n", atoi(&l_byte));
        printf("pure int %d\n", buffer[c_info->metaint - bytes_to_meta]);
        */

        //int x;
        //for (x = 0; x < bytes_received; x++){ printf("%c - %d\n", buffer[x], x); }
        //for (x = c_info->metaint - bytes_to_meta + 1; x < c_info->metaint - bytes_to_meta + meta_length; x++){ printf("%c", buffer[x]); }

        if (bytes_received - meta_length < 0){
          memcpy(meta_info, buffer + c_info->metaint - bytes_to_meta + 1, bytes_received - bytes_to_meta + 1);
          filled_meta_length = bytes_received - bytes_to_meta + 1;
          getting_meta = 1;
        }
        else {
          // all meta is contained here in maybe have some music at the end
          memcpy(meta_info, buffer + c_info->metaint - bytes_to_meta + 1, meta_length);
          meta_info[meta_length] = '\0';

          printf("\nMETADATA: \n%s\n", meta_info);

          // some music at the end
          bytes_to_meta = bytes_received - (c_info->metaint - bytes_to_meta) - meta_length - 1;
          printf("bytes_to_meta %d\n", bytes_to_meta);
        }
      }
      else {
        // it's all music
        bytes_to_meta += bytes_received;
          //printf("bytes_to_meta %d\n", bytes_to_meta);
      }
    }
    else {
      // getting rest of the meta
      printf("YOHOOO\n");
      memcpy(meta_info + filled_meta_length, buffer, meta_length - filled_meta_length);
      buffer[meta_length] = '\0';
      printf("METADATA: %s\n", buffer);

      getting_meta = 0;
      bytes_to_meta = bytes_received + meta_length - filled_meta_length;
    }
  }
}

void 
spellcast_client_cleanup(spellcast_con_info *c_info)
{
  close(c_info->sock_d);
  free(c_info);
}
