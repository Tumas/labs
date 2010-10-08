#ifndef __SPELLCAST_CLIENT_H__
#define __SPELLCAST_CLIENT_H__

typedef struct _con_info {
  int metaint;
  int bitrate;
  int sock_d;
  int needs_meta;
} spellcast_con_info;

typedef struct _cacheable_buffer {
  char *buffer;
  int buf_start;
  int bufflen;
} spellcast_cache;

void 
spellcast_client_print_connection_info(const spellcast_con_info *c_info);
void 
spellcast_client_cleanup(spellcast_con_info *c_info);
void 
spellcast_client_play(spellcast_con_info *c_info);
int 
spellcast_get_server_information(spellcast_con_info *sp);

#endif
