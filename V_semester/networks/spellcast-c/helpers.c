#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/socket.h>

#include "helpers.h"

char*
spellcast_allocate_string(char *str)
{
  char *n_str = (char*) malloc(strlen(str) + 1);

  if (n_str == NULL){
    P_ERROR("malloc gave NULL (creating new string)");
    return NULL; 
  }

  strcpy(n_str, str);
  return n_str;
}

int 
send_message(int socket, char *message, int len)
{
  int total = 0;
  int bytes_left = len;
  int n;

  while (total < len){
    n = send(socket, message + total, bytes_left, 0);

    if (n == -1){
      perror("send");
      break;
    }

    total += n;
    bytes_left -= n;
  }

  return total;
}

void
str_to_lower(char *str)
{
  while (*str) { 
    (*str) = tolower(*str);
    str++;
  }
}

