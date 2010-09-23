#ifndef __SPELLCAST_SERVER_SOURCE_H__
#define __SPELLCAST_SERVER_SOURCE_H__

#include "server.h"

/**/
int spellcast_accept_source(spellcast_server* srv);

/**/
source_meta* spellcast_create_empty_source(int);

/**/
void spellcast_register_source(spellcast_server*, source_meta*);

/**/
source_meta* spellcast_get_source(spellcast_server*, int);

/**/
void spellcast_disconnect_source(spellcast_server*, source_meta*);

/**/
void spellcast_dispose_source(source_meta*);

/**/
int spellcast_source_parse_header(spellcast_server*, source_meta*);

/**/
void spellcast_print_source_info(source_meta*);

/**/
int spellcast_source_mountpoint_taken(spellcast_server*, source_meta*);

#endif
