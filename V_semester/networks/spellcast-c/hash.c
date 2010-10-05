#include <stdio.h>
#include <stdlib.h>
#include "hash.h"
#include "list.h"

static int
hash_key(sp_hash_table *ht, int key)
{
  return key % ht->buckets; 
}

int 
spellcast_htable_init(sp_hash_table *ht, int buckets, int (*match) (int key, const void *data2),
    void (*destroy) (void *data))
{
  int i;

  if ((ht->table = (DList*) malloc(buckets * sizeof(DList))) == NULL){
    return -1; 
  }

  for (i = 0; i < buckets; i++){
    dlist_init(&ht->table[i], destroy, NULL);
  }

  ht->buckets = buckets;
  ht->destroy = destroy;
  ht->match = match;
  ht->size = 0;

  return 0;
}

int
spellcast_htable_insert(sp_hash_table *ht, int key, const void *data)
{
  void *check_data = NULL;
  int bucket, retval;

  if (spellcast_htable_lookup(ht, key, &check_data) == 0){
    return 1;
  }

  bucket = hash_key(ht, key); 
  retval = dlist_insert(&ht->table[bucket], data);

  if (retval == 0)
    ht->size++;
  
  return retval;
}

int
spellcast_htable_lookup(sp_hash_table *ht, int key, void **data)
{
  int bucket = hash_key(ht, key); 
  listElement *element;

  for (element = dlist_head(&ht->table[bucket]); element != NULL; element = dlist_next(element)){
    if (ht->match(key, dlist_data(element))){
      *data = dlist_data(element);
      return 0;
    }
  }

  *data = NULL;
  return -1;
}

int 
spellcast_htable_remove(sp_hash_table *ht, int key, void **data)
{
  int bucket = hash_key(ht, key); 
  listElement *element;

  for (element = dlist_head(&ht->table[bucket]); element != NULL; element = dlist_next(element)){
    if (ht->match(key, dlist_data(element))){
      if (dlist_remove(&ht->table[bucket], element, data) == 0){
        ht->size--;
        return 0;
      }
      else {
        return -1;
      }
    }
  }

  return -1;
}

void 
spellcast_htable_destroy(sp_hash_table *ht)
{
  int i;

  for (i = 0; i < ht->buckets; i++){
    dlist_destroy(&ht->table[i]);
  }

  free(ht->table);
}


/* Hash iterator functions */
hash_iterator*
spellcast_create_hash_iterator(sp_hash_table* htable)
{
  hash_iterator *h_iter = (hash_iterator*) malloc(sizeof(hash_iterator));

  h_iter->index = 0;
  h_iter->inner_index = 0;
  h_iter->htable = htable;

  return h_iter;
}

void*
spellcast_hash_iterator_next(hash_iterator *h_iter)
{
  int bucket, i;
  listElement *element;

  while (h_iter->index != h_iter->htable->buckets){
    bucket = hash_key(h_iter->htable, h_iter->index);

    while (dlist_size(&h_iter->htable->table[bucket]) != h_iter->inner_index){
      element = dlist_head(&h_iter->htable->table[bucket]);

      for (i = 0; i < h_iter->inner_index; i++){
        element = dlist_next(element);
      }

      h_iter->inner_index++;
      return dlist_data(element);
    }

    h_iter->inner_index = 0;
    h_iter->index++;
  }

  h_iter->index = 0;
  return NULL;
}
