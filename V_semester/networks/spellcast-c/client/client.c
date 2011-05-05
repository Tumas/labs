#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <string.h>

#include "../fmod/api/inc/fmod.h"
#include "../fmod/api/inc/fmod_errors.h"

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
 *    playback
 *
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
  int rv;

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

        return 0;
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

  freeaddrinfo(servinfo);

  if (p == NULL){
    fprintf(stderr, "Client: failed to connect\n");
    return 1;
  }

  /* Send information about client */
  char buffer[1000];
  sprintf(buffer, ICY_CLIENT2SRV_MESSAGE, m_point, "HTTP/1.0", url, port, "TEST agent", 1, "close");
  send_message(c_info->sock_d, buffer, strlen(buffer));

  /* Parse information from server */
  if (spellcast_get_server_information(c_info) != 0){
    spellcast_client_cleanup(c_info);
    return 0;
  }

  spellcast_client_print_connection_info(c_info);

  printf(" Starting playback ... \n");
  spellcast_client_play(c_info);
  spellcast_client_cleanup(c_info);
  printf(" Finishing playback ... \n");

  return 0;
}


/*
 * Return value
 *  0 - no mistakes, all information succesfully parsed
 *  1 - no mistakes, but parsing is not finished. Could not get all response
 *  -1 - mistake happened
 *  -2 - wrong server response code
 */
static int 
spellcast_client_parse_server_response(spellcast_cache *rsp, spellcast_con_info *sp)
{
   int is_full_message = strstr(rsp->buffer, SPELLCAST_HEADER_END_TOKEN) == NULL ?  0 : 1;
   char *header_line = strtok(rsp->buffer, SPELLCAST_LINE_TOKEN);
   char *match, *prev_line;

   while (header_line != NULL){
     str_to_lower(header_line);
     printf("Parsed line  %s\n", header_line);

     if ((match = strstr(header_line, SPELLCAST_BR_TOKEN)) != NULL){
       sp->bitrate = atoi(match + strlen(SPELLCAST_BR_TOKEN));
     }
     else if ((match = strstr(header_line, SPELLCAST_METAINT_CLIENT_TOKEN)) != NULL){
       sp->metaint = atoi(match + strlen(SPELLCAST_METAINT_CLIENT_TOKEN));
     }

     prev_line = header_line;
     header_line = strtok(NULL, SPELLCAST_LINE_TOKEN);
   }

   if (!is_full_message){
     rsp->buf_start = strlen(prev_line);
     strcpy(rsp->buffer, prev_line);

     printf("Cached buffer %d %s\n", rsp->buf_start, rsp->buffer);
     return 1;
   }

   return 0;
}

int 
spellcast_get_server_information(spellcast_con_info *c_info)
{
  // TODO: wrap this into a abstract data type
  spellcast_cache *buffer = (spellcast_cache*) malloc(sizeof(spellcast_cache));
  buffer->buffer = (char*) malloc(BUFFER_SIZE * sizeof(char));
  buffer->bufflen = BUFFER_SIZE;
  buffer->buf_start = 0;

  int bytes_received = 0;
  int ret_val;

  do {
    printf("CURRENT BUFFER size and start point : %d %d\n", buffer->bufflen, buffer->buf_start);

    if (buffer->buf_start == buffer->bufflen - 1){
      // we have all buffer cached 
      //  so we need to expand it!
      char temp_buffer[buffer->bufflen];
      int temp_size = buffer->bufflen;
      memcpy(temp_buffer, buffer->buffer, temp_size);

      free(buffer->buffer);
      buffer->buffer = (char*) malloc(buffer->bufflen * 2 * sizeof(char));
      buffer->bufflen *= 2;
      memcpy(buffer->buffer, temp_buffer, temp_size);

      printf("expanding buffer: size %d, start %d\n", buffer->bufflen, buffer->buf_start);
    }

    if ((bytes_received = recv(c_info->sock_d, buffer->buffer + buffer->buf_start, buffer->bufflen - 1 - buffer->buf_start, 0)) < 1){
      printf(" bytes receiied %d\n", bytes_received);
      perror("recv");

      free(buffer->buffer);
      free(buffer);
      return -1;
    }

    buffer->buffer[buffer->buf_start + bytes_received] = '\0';
    printf("SERVER RESPONDED with %d bytes\n\n%s\n", bytes_received, buffer->buffer);

  } while ((ret_val = spellcast_client_parse_server_response(buffer, c_info)) == 1);

  free(buffer->buffer);
  free(buffer);
  return ret_val;
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
  int meta_length, filled_meta_length, getting_meta = 0;
  long bytes_to_meta = 0;

  FILE *snd_file = fopen("snd.mp3", "a+");
  //FILE *snd_file = tmpfile();

  /*
  FMOD_SYSTEM     *system;
  FMOD_SOUND      *sound;
  FMOD_CHANNEL    *channel = 0;
  FMOD_RESULT      result;

  result = FMOD_System_Create(&system);
  result = FMOD_System_Init(system, 1, FMOD_INIT_NORMAL, NULL);
  result = FMOD_System_CreateSound(system, "snd.mp3", FMOD_SOFTWARE | FMOD_2D | FMOD_CREATESTREAM, 0, &sound);
  */

  while ((bytes_received = recv(c_info->sock_d, buffer, BUFFER_SIZE, 0)) > 0){

    if (!getting_meta) {
      if (bytes_received + bytes_to_meta > c_info->metaint){
        // position of metadata length byte in buffer
        int len_pos = c_info->metaint - bytes_to_meta;

        /*
        printf("metaint %d\n", c_info->metaint);
        printf("bytes_received %d\n", bytes_received);
        printf("bytes_to_meta %ld\n", bytes_to_meta);
        printf("bytes_received + bytes_to_meta %ld\n", bytes_received + bytes_to_meta);
        printf("meta starts at %d - %ld : %ld\n", c_info->metaint, bytes_to_meta, len_pos);
        */

        fwrite(buffer, 1, len_pos, snd_file);
        meta_length = buffer[c_info->metaint - bytes_to_meta] * 16;

        if (meta_length != 0){
          // check if all metadata is in buffer
          if (bytes_received - meta_length < 0){
            memcpy(meta_info, buffer + len_pos + 1, bytes_received - (len_pos + 1));

            filled_meta_length = bytes_received - (len_pos + 1);
            getting_meta = 1;
          }
          else {
            // all meta is contained within buffer, so we probably have some music at the end
            memcpy(meta_info, buffer + len_pos + 1, meta_length);
            meta_info[meta_length] = '\0';

            printf("METADATA: %s\n", meta_info);

            // some music at the end
            // music at the end = buffer len - meta len - data up to meta - 1 (size of meta byte)
            bytes_to_meta = bytes_received - len_pos - 1 - meta_length;
            fwrite(buffer + len_pos + 1 + meta_length, 1, bytes_to_meta, snd_file);
          }
        } 
        else {
          // it's all music but we need to reset the counter since server provided no metadata
          fwrite(buffer + len_pos + 1, 1, bytes_received - 1 - len_pos, snd_file);

          bytes_to_meta += bytes_received - 1;
          bytes_to_meta %= c_info->metaint;
        }
      }
      else {
        // it's all music
        bytes_to_meta += bytes_received;
        fwrite(buffer, 1, bytes_received, snd_file);
      }
    }
    else {
      // getting rest of the meta
      if (meta_length - filled_meta_length < bytes_received){
        // all the rest of metadata is inside received bytes
        memcpy(meta_info + filled_meta_length, buffer, meta_length - filled_meta_length);
        meta_info[meta_length] = '\0';
        printf("METADATA: %s\n", meta_info);
         
        // + a little bit of music at the end
        bytes_to_meta = bytes_received - meta_length + filled_meta_length;
        fwrite(buffer + meta_length - filled_meta_length, 1, bytes_to_meta, snd_file);
        getting_meta = 0;
      }
      else {
        memcpy(meta_info + filled_meta_length, buffer, bytes_received);
        filled_meta_length += bytes_received;
      }
    }
  }

  fclose(snd_file);
  printf("Disconnected from the server: (client received %d)\n", bytes_received);
}

void 
spellcast_client_cleanup(spellcast_con_info *c_info)
{
  close(c_info->sock_d);
  free(c_info);
}
