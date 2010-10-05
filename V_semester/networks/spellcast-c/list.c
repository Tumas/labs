#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "list.h"

void dlist_init(DList *list, void (*destroy)(void *data), int (*compare) (void *key, void *key2))
{
   list->size = 0;
   list->destroy = destroy;
   list->compare = compare;
   list->head = NULL;
   list->tail = NULL;
}

void dlist_destroy(DList *list)
{
    void *data;
    while (dlist_size(list) > 0){
        if (dlist_remove(list, dlist_tail(list), (void**)&data) == 0 && list->destroy != NULL){
            //saukiam vartotojo nustatyta funkcija destroy dinaminei atminciai atlaisvinti
            list->destroy(data);
        }
    }
}

int dlist_insert(DList *list, const void *data)
{
    listElement *new_element;

    //issiskiriam vietos naujam elementui
    if ((new_element = (listElement*) malloc(sizeof(listElement))) == NULL)
        return(-1);
    new_element->data = (void*)data;

    if (dlist_size(list) == 0){
        //iterpimas i tuscia sarasa
        list->head = new_element;
        list->tail = new_element;
        new_element->prev = NULL;
        new_element->next = NULL;
    }
    else {
        //iterpimas i netuscia sarasa
        //iterpiame i saraso pabaiga
        new_element->next = NULL;
        new_element->prev = list->tail;
        list->tail->next = new_element;
        list->tail = new_element;
    }
    //Atnaujinam saraso dydi
    list->size++;
    return(0);
}

int dlist_remove(DList *list, listElement *element, void **data)
{
    //NULL gali buti tik kai sarasas tuscias
    if (element == NULL && dlist_size(list) != 0)
        return(-1);

    *data = element->data;
    if (element == list->head){
        //pasalinam is pradzios
        list->head = element->next;
        if (list->head != NULL)
            element->next->prev = NULL;
    } 
    else {
        //pasalinam ne is pradzios
        element->prev->next = element->next;
        if (element->next == NULL)
            list->tail = element->prev;
        else
            element->next->prev = element->prev;
    }
    //atlaisvinam atminti reikalinga saraso elementui
    free(element);
    list->size--;
    return(0);
}

int 
dlist_remove_by_data(DList *list, void *lookup, void **data)
{
  listElement *element = dlist_head(list);
  while (element != NULL){
    if (dlist_data(element) == lookup){
      return dlist_remove(list, element, data);
    }

    element = dlist_next(element);
  }

  return -1;
}

/*  ***********************************************     */
static listElement* get_element(DList *list, int n)
{
    if (n < 0 || n > dlist_size(list)-1)
        return (NULL);
    int i = 0;
    listElement *temp = list->head;
    
    for (; i < n ; i++)
        temp = temp->next;
    return(temp);
}

static void swap_pointers(listElement *a, listElement *b)
{
    void *tmp = dlist_data(a);
    dlist_data(a) = dlist_data(b);
    dlist_data(b) = tmp;
}

static int partition(DList *list, int left, int right)
{
    void *x = dlist_data(get_element(list, right));
    int i = left - 1;
    int j;

    for (j = left; j != right; j++){
        if (list->compare(dlist_data(get_element(list, j)), x) <= 0){
            i++;
            swap_pointers(get_element(list, i), get_element(list, j));
        }
    }
    swap_pointers(get_element(list, i+1), get_element(list, right));
    return(i+1);
}

void dlist_qsort(DList *list, int left, int right)
{
    if (left < right){
        int q = partition(list, left, right);
        dlist_qsort(list, left, q-1);
        dlist_qsort(list, q+1, right);
    }
}

