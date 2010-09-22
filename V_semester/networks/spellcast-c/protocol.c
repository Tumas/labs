#include <stdlib.h>
#include <stdio.h>

#include "helpers.h"
#include "protocol.h"

icy_protocol*
spellcast_init_icy_protocol_info()
{
  icy_protocol* icy_p = (icy_protocol*) malloc(sizeof(icy_protocol));

  if (icy_p == NULL){
    P_ERROR("malloc gave NULL (intializing protocol information)");
    return NULL; 
  }

  icy_p->source_header = (source_header_info*) malloc(sizeof(source_header_info));

  icy_p->source_header->source_sep = spellcast_allocate_string(SPELLCAST_SOURCE_TOKEN);
  icy_p->source_header->authorization_sep = spellcast_allocate_string(SPELLCAST_AUTH_TOKEN);
  icy_p->source_header->user_agent_sep = spellcast_allocate_string(SPELLCAST_USER_AGENT_TOKEN);
  icy_p->source_header->content_type_sep = spellcast_allocate_string(SPELLCAST_CONTENT_TYPE_TOKEN);
  icy_p->source_header->name_sep = spellcast_allocate_string(SPELLCAST_NAME_TOKEN);
  icy_p->source_header->url_sep = spellcast_allocate_string(SPELLCAST_URL_TOKEN);
  icy_p->source_header->description_sep = spellcast_allocate_string(SPELLCAST_DESCRIPTION_TOKEN);
  icy_p->source_header->genre_sep = spellcast_allocate_string(SPELLCAST_GENRE_TOKEN);
  icy_p->source_header->bitrate_sep = spellcast_allocate_string(SPELLCAST_BITRATE_TOKEN);
  icy_p->source_header->samplerate_sep = spellcast_allocate_string(SPELLCAST_SAMPLERATE_TOKEN);
  icy_p->source_header->channel_sep = spellcast_allocate_string(SPELLCAST_CHANNEL_TOKEN);
  icy_p->source_header->public_sep = spellcast_allocate_string(SPELLCAST_PUBLIC_TOKEN);

  return icy_p;
}

void
spellcast_dispose_icy_protocol_info(icy_protocol *i)
{
  free(i->source_header->public_sep);
  free(i->source_header->channel_sep);
  free(i->source_header->samplerate_sep);
  free(i->source_header->bitrate_sep);
  free(i->source_header->genre_sep);
  free(i->source_header->description_sep);
  free(i->source_header->url_sep);
  free(i->source_header->name_sep);
  free(i->source_header->content_type_sep);
  free(i->source_header->user_agent_sep);
  free(i->source_header->authorization_sep);
  free(i->source_header->source_sep);

  free(i->source_header);
  free(i);
}
