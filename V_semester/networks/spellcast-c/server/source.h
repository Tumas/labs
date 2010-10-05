#ifndef __SPELLCAST_SERVER_SOURCE_H__
#define __SPELLCAST_SERVER_SOURCE_H__

#include "server.h"

int spellcast_accept_source(spellcast_server* srv);
source_meta* spellcast_allocate_space_for_empty_source(int);
int spellcast_register_source(spellcast_server*, source_meta*);
void spellcast_dispose_source(void*);
void spellcast_print_source_info(source_meta*);
source_meta* spellcast_get_source_by_mountpoint(spellcast_server *, char*);
source_meta* spellcast_get_random_source(spellcast_server *srv);
void spellcast_disconnect_source(spellcast_server*, source_meta*);

#endif
