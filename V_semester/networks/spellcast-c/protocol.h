#ifndef __SPELLCAST_PROTOCOL_H__
#define __SPELLCAST_PROTOCOL_H__

#define SPELLCAST_LINE_TOKEN "\r\n"
#define SPELLCAST_HEADER_END_TOKEN SPELLCAST_LINE_TOKEN SPELLCAST_LINE_TOKEN 
#define SPELLCAST_HEADER_INFO_SEPARATOR ":"
// ?? whats with the colons

#define SPELLCAST_SOURCE_TOKEN "source"
#define SPELLCAST_CLIENT_TOKEN "get"
#define SPELLCAST_AUTH_TOKEN "authorization:"
#define SPELLCAST_USER_AGENT_TOKEN "user-agent:"
#define SPELLCAST_CONTENT_TYPE_TOKEN "content-type:"
#define SPELLCAST_NAME_TOKEN "ice-name:"
#define SPELLCAST_URL_TOKEN "ice-url:"
#define SPELLCAST_DESCRIPTION_TOKEN "ice-description:"
#define SPELLCAST_GENRE_TOKEN "ice-genre:"
#define SPELLCAST_BITRATE_TOKEN "bitrate="
#define SPELLCAST_SAMPLERATE_TOKEN "samplerate"
#define SPELLCAST_CHANNEL_TOKEN "channels"
#define SPELLCAST_PUBLIC_TOKEN "ice-public:"
#define SPELLCAST_METAINT_TOKEN "icy-metadata:"

#define ICY_METADATA_FORMAT "%cStreamTitle='%s';StreamUrl='%s';"
#define SPELLCAST_SRV2SRC_OK_MSG "ICY 200 OK" SPELLCAST_HEADER_END_TOKEN
#define ICY_SRV2CLIENT_MESSAGE "ICY 200 OK\r\n\
icy-notice:%s\r\n\
icy-name:%s\r\n\
icy-genre:%s\r\n\
icy-url:%s\r\n\
Content-Type:%s\r\n\
icy-pub:%d\r\n\
icy-br:%d\r\n\
icy-metaint:%d\r\n\r\n"

typedef struct _icy_metadata {
  char *url;
  char *title;
} icy_metadata;

#endif
