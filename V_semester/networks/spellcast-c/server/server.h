#ifndef __SPELLCAST_SERVER_H__
#define __SPELLCAST_SERVER_H__

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <string.h>

#include "../helpers.h"
#include "../protocol.h"

#include "../list.h"
#include "../hash.h"

#define BUFFLEN 8192
#define METAINT 8192
#define CHAR_BUFFLEN BUFFLEN - 1

#define MAX_CLIENTS 10
#define MAX_SOURCES 5

#define SOURCEBACKLOG 5
#define CLIENTBACKLOG 5
#define HASHSIZE 89

#define CPOINT_BUFFER_SIZE(c_point) (BUFFLEN - c_point->buf_start > 0 ? BUFFLEN - c_point->buf_start : BUFFLEN)
#define CHAR_CPOINT_BUFFER_SIZE(c_point) (CHAR_BUFFLEN - c_point->buf_start > 0 ? CHAR_BUFFLEN - c_point->buf_start : CHAR_BUFFLEN)

typedef struct _stream_meta {
  char *name;
  char *genre; 
  char *url; 
  char *mime_type;
  unsigned int bitrate;
  unsigned short pub;
} stream_meta;

typedef struct _connection_point {
  stream_meta *meta; 
  char *mountpoint;
  int sock_d;

  int buf_start;
  char buffer[BUFFLEN];
  char *description;
} connection_point;

typedef struct _source_meta {
  connection_point *c_point;

  int connected_clients;
  DList *clients;
} source_meta;

typedef struct _client_meta {
  connection_point *c_point;

  int metaint;
  int bytes_to_meta;
} client_meta;

typedef struct _spellcast_server {
  stream_meta *meta;
  char *notice;
  unsigned int metaint; 

  char *source_port;
  char *client_port;

  struct addrinfo hints;
  struct addrinfo *srv_src_addrinfo, *srv_cl_addrinfo;

  // sockets
  int src_sock;
  int cl_sock;
  int latest_sock;

  fd_set master_read;
  fd_set empty_sources;
  fd_set empty_clients;

  sp_hash_table *clients;
  sp_hash_table *sources;

  int connected_sources;
  int connected_clients;
} spellcast_server;

void spellcast_print_server_info(const spellcast_server*);
void spellcast_print_server_stats(const spellcast_server*);
int spellcast_init_server(spellcast_server*);
void spellcast_server_dispose(spellcast_server*);
int spellcast_server_run(spellcast_server*);
void *get_in_addr(struct sockaddr*);
void spellcast_dispose_stream_meta(stream_meta*);
int spellcast_parse_header(connection_point* cnp, void *object, void (*callback)(char *, void*));
int  spellcast_send_metadata(int socket, icy_metadata *meta);

#endif
