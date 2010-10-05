#ifndef __SPELLCAST_HASH_TABLE__
#define __SPELLCAST_HASH_TABLE__

#include "list.h"

/* Simple implementation of CHAINED HASH TABLE */

typedef struct _sp_HT {
  void (*destroy) (void *data);
  int (*match) (const int key, const void *data2);

  int buckets;
  int size;
  DList *table;
} sp_hash_table;

typedef struct _hash_iterator {
  sp_hash_table *htable;
  int index;
  int inner_index;
} hash_iterator;

/* FUNCTIONS */

/*
 * Init hash table. 
 * Return: -1 if init failed
 *          0 if init was succesfull
 */
int spellcast_htable_init(sp_hash_table *, int, 
int (*match) (int key, const void *data2), void (*destroy) (void *data));

int spellcast_htable_insert(sp_hash_table *, int, const void *);
int spellcast_htable_remove(sp_hash_table *, int, void **);
int spellcast_htable_lookup(sp_hash_table *, int, void **);
void spellcast_htable_destroy(sp_hash_table *);


/* Hash iterator */
hash_iterator* spellcast_create_hash_iterator(sp_hash_table*);
void* spellcast_hash_iterator_next(hash_iterator*);

#endif
