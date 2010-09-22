#include <stdlib.h>
#include <stdio.h>
#include <string.h>

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
