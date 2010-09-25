#ifndef __SPELLCAST_HELPERS_H__
#define __SPELLCAST_HELPERS_H__

#define P_ERROR(str) fprintf(stderr, "Error occurred: %s\n", str)
#define P_WARN(str) fprintf(stdout, "Warning: %s\n", str)

char *spellcast_allocate_string(char *);
int send_message(int, char*, int);

#endif
