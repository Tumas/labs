#ifndef __SPELLCAST_SERVER_CLIENT_H__
#define __SPELLCAST_SERVER_CLIENT_H__

#include "server.h"

int spellcast_accept_client(spellcast_server*);

client_meta* spellcast_allocate_space_for_empty_client(int);

void spellcast_dispose_client(void *);

void spellcast_print_client_info(client_meta*);

void spellcast_client_header_parse_callback(char *, void *);

void spellcast_disconnect_client(spellcast_server*, client_meta*);

#endif
