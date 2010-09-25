#ifndef __SPELLCAST_SERVER_CLIENT_H__
#define __SPELLCAST_SERVER_CLIENT_H__

#include "server.h"

/**/
int spellcast_accept_client(spellcast_server*);

/**/
client_meta* spellcast_create_empty_client(int);

/**/
void spellcast_register_client(spellcast_server*, client_meta*);

/**/
void spellcast_dispose_client(client_meta *);

/**/
void spellcast_disconnect_client(spellcast_server*, client_meta*);

/**/
void spellcast_print_client_info(client_meta*);

/**/
int spellcast_client_parse_header(spellcast_server*, client_meta*);

/**/
client_meta* spellcast_get_client(spellcast_server*, int);

/**/
void spellcast_add_empty_client(spellcast_server*, client_meta *);

/**/
source_meta* spellcast_get_source_by_mountpoint(spellcast_server*, char*);

#endif
