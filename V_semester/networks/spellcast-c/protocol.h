#ifndef __SPELLCAST_PROTOCOL_H__
#define __SPELLCAST_PROTOCOL_H__

#define SPELLCAST_LINE_TOKEN "\r\n"
#define SPELLCAST_HEADER_END_TOKEN "\r\n\r\n"

#define SPELLCAST_HEADER_INFO_SEPARATOR ":"
#define SPELLCAST_SRV2SRC_OK_MSG "ICY 200 OK\r\n\r\n"

#define SPELLCAST_SOURCE_TOKEN "SOURCE"
#define SPELLCAST_AUTH_TOKEN "Authorization:"
#define SPELLCAST_USER_AGENT_TOKEN "User-Agent:"
#define SPELLCAST_CONTENT_TYPE_TOKEN "Content-Type:"
#define SPELLCAST_NAME_TOKEN "ice-name:"
#define SPELLCAST_URL_TOKEN "ice-url:"
#define SPELLCAST_DESCRIPTION_TOKEN "ice-description:"
#define SPELLCAST_GENRE_TOKEN "ice-genre:"
#define SPELLCAST_BITRATE_TOKEN "bitrate="
#define SPELLCAST_SAMPLERATE_TOKEN "samplerate"
#define SPELLCAST_CHANNEL_TOKEN "channels"
#define SPELLCAST_PUBLIC_TOKEN "ice-public:"

#define ICY_METADATA_FORMAT "%cStreamTitle='%s';StreamUrl='%s';"
#define ICY_SRV2CLIENT_MESSAGE "ICY 200 OK\r\n\
  icy-notice:%s\r\n\
  icy-name:%s\r\n\
  icy-genre:%s\r\n\
  icy-url%s\r\n\
  Content-Type:%s\r\n\
  icy-pub:%d\r\n\
  icy-br:%d\r\n\
  icy-metaint:%d\r\n\r\n"

typedef struct _source_header_info {
  char *source_sep;
  char *authorization_sep;
  char *user_agent_sep;
  char *content_type_sep;
  char *name_sep;
  char *url_sep;
  char *description_sep;
  char *genre_sep;
  char *bitrate_sep;
  char *samplerate_sep;
  char *channel_sep; 
  char *public_sep;
  char *header_end;
} source_header_info;

typedef struct _icy_protocol {
  source_header_info *source_header;
  char *ok_message;
} icy_protocol;

typedef struct _icy_metadata {
  char *url;
  char *title;
} icy_metadata;

/**/
icy_protocol* spellcast_init_icy_protocol_info();

/**/
void spellcast_dispose_icy_protocol_info(icy_protocol*);

#endif
