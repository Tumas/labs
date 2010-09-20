#ifndef __SPELLCAST_SERVER_H__
#define __SPELLCAST_SERVER_H__

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <unistd.h>
#include <string.h>

/*
#include "source.h"
#include "client.h"
*/

#define BUFFLEN 1024
#define MAX_CLIENTS 10
#define MAX_SOURCES 5
#define SOURCEBACKLOG 5
#define CLIENTBACKLOG 5

#define P_ERROR(str) fprintf(stderr, "Error occurred: %s\n", str)
#define P_WARN(str) fprintf(stdout, "Warning: %s\n", str)

typedef struct _stream_meta {
  char *name;
  char *genre;
  char *url;
  char *mime_type;
  unsigned int bitrate;
  unsigned short pub;
} stream_meta;

typedef struct _source_meta {
  stream_meta *stream_data;
  int sock_d;
  char *mountpoint;
  char buffer[BUFFLEN];
} source_meta;

typedef struct _client_meta {
  char *name;
  int sock_d;
} client_meta;

typedef struct _server_meta {
  stream_meta *server_data;
  char *notice;
  unsigned int metaint;
} server_meta;

typedef struct _spellcast_server {
  server_meta *server_metadata;
  char *source_port;
  char *client_port;
  struct addrinfo hints;
  struct addrinfo *srv_src_addrinfo, *srv_cl_addrinfo;
  int src_sock;
  int cl_sock;
  fd_set master_read;
  int latest_sock;

  client_meta* clients[MAX_CLIENTS];
  source_meta* sources[MAX_SOURCES];

  int connected_sources;
  int connected_clients;
} spellcast_server;

/* print information about server */
void spellcast_print_server_info(const spellcast_server*);

/* initialize server properties like name,  */
spellcast_server* spellcast_init_server_variables(const char *, const char*, const server_meta*);

/**/
int spellcast_init_server(spellcast_server *);

/**/
int spellcast_server_run(spellcast_server *);

/**/
void spellcast_server_dispose(spellcast_server *);

#endif
