#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>
#include <stdio.h>

#include "../hash.h"

static int 
test_match(int key, const void *data2)
{
  return key == atoi((char*) data2);
}

static int 
false_test_match(int key, const void *data2)
{
  return 0;
}


static void
test_hash_creation()
{
  sp_hash_table *ht = (sp_hash_table*) malloc(sizeof(sp_hash_table));

  assert(spellcast_htable_init(ht, 100, test_match, free) == 0 && "test_hash_creation()");
  assert(ht->size == 0);
  assert(ht->buckets == 100);
  assert(ht->destroy == free);

  spellcast_htable_destroy(ht);
  free(ht);
}

static void 
test_hash_insert()
{
  sp_hash_table *ht = (sp_hash_table*) malloc(sizeof(sp_hash_table));
  spellcast_htable_init(ht, 93, test_match, NULL);

  char *str1 = "111";
  char *str2 = "222";

  spellcast_htable_insert(ht, 1, (void*) str1);
  spellcast_htable_insert(ht, 2, (void*) str2);
  
  assert(ht->size == 2);
  spellcast_htable_destroy(ht);
  free(ht);
}

static void 
test_hash_insert_duplicate()
{
  sp_hash_table *ht = (sp_hash_table*) malloc(sizeof(sp_hash_table));
  spellcast_htable_init(ht, 93, test_match, NULL);

  char *str1 = "1";

  spellcast_htable_insert(ht, 1, (void*) str1);
  spellcast_htable_insert(ht, 1, (void*) str1);
  
  assert(ht->size == 1);

  spellcast_htable_destroy(ht);
  free(ht);
}


static void 
test_hash_remove()
{
  sp_hash_table *ht = (sp_hash_table*) malloc(sizeof(sp_hash_table));
  spellcast_htable_init(ht, 93, test_match, NULL);

  char *str1 = "1";
  char *str2; 

  spellcast_htable_insert(ht, 1, (void*) str1);
  spellcast_htable_remove(ht, 1, (void**) &str2);
  
  assert(ht->size == 0);

  spellcast_htable_destroy(ht);
  free(ht);
}

static void 
test_hash_lookup()
{
  sp_hash_table *ht = (sp_hash_table*) malloc(sizeof(sp_hash_table));
  spellcast_htable_init(ht, 93, test_match, NULL);

  char *str1 = "1";
  char *str2; 

  spellcast_htable_insert(ht, 1, (void*) str1);
  assert(spellcast_htable_lookup(ht, 1, (void**) &str2)  == 0);
  assert(ht->size == 1);
  assert(str2 != NULL);

  spellcast_htable_destroy(ht);
  free(ht);
}

static void 
test_hash_iterator()
{
  sp_hash_table *ht = (sp_hash_table*) malloc(sizeof(sp_hash_table));
  spellcast_htable_init(ht, 93, false_test_match, NULL);

  char *str1 = "123";
  char *str2 = "123"; 

  spellcast_htable_insert(ht, 1, (void*) str1);
  spellcast_htable_insert(ht, 1, (void*) str2);
  assert(ht->size == 2);

  hash_iterator *h_iter = spellcast_create_hash_iterator(ht);
  void *x;
  
  while ((x = spellcast_hash_iterator_next(h_iter)) != NULL){
    assert(strcmp((char*)x, str1) == 0);
  }

  free(h_iter);
  spellcast_htable_destroy(ht);
  free(ht);
}

int main()
{
  test_hash_creation();
  test_hash_insert();
  test_hash_insert_duplicate();
  test_hash_remove();
  test_hash_lookup();
  test_hash_iterator();

  return 0;
}
