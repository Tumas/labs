#ifndef DOUBLE_LINKED_LIST
#define DOUBLE_LINKED_LIST

/*
 *      Saraso elementai laiko tik nuoroda i duomenis, o ne juos pacius todel kur tuos duomenis laikyti
 *      privalo pasirupinti pats vartotojas
 */

/*  Dvipusio saraso elementas */
typedef struct listElement_ {
    void *data;
    struct listElement_ *next;
    struct listElement_ *prev;
} listElement;

/*  Dvipusis sarasas  */
typedef struct dlist_ {
    int size;                                       /* kiek elementu yra sarase */
    void (*destroy) (void *data);                   //vartotojo nurodyta funkcija atminties atlaisvinimui. Jei saraso elementai nenaudoja dinamines
                                                    //atminties, tuomet destroy turetu buti NULL
    int (*compare) (void *key1, void *key2);        //vartotojo nustatyta elementu lyginimo funkcija

    listElement *head;      //rodykle i saraso pirma elementa
    listElement *tail;      //rodykle i saraso paskutini elementa
} DList;


#define dlist_size(list) ((list)->size)
#define dlist_data(element) ((element)->data)
#define dlist_next(element) ((element)->next)
#define dlist_prev(element) ((element)->prev)
#define dlist_head(list) ((list)->head)
#define dlist_tail(list) ((list)->tail)
#define dlist_is_head(element) ((element)->prev == NULL ? 1 : 0)
#define dlist_is_tail(element) ((element)->next == NULL ? 1 : 0)

/**************     OPERACIJOS      **********************/

/*  Saraso inicijavimo operacija. Funkcija turi buti kvieciama pries naudojant sarasa.  
 *  Funkcija inicijuoja sarasa kuris perduodamas parametru list.
 *  Du like parametrai yra vartotojo suteikiamas funkcijos:
 *      Saraso elemento naudojamos dinamines atminties atlaisvinimo
 *      Saraso elemento lyginimo
 *          funkcija turi grazinti 0 jei elementai lygus,
 *          >0 jei pirmas didesnis
 *          <0 jei pirmas mazesnis
 */
void dlist_init(DList *list, void (*destroy)(void *data), int (*compare) (void *key, void *key2));

/*  Istrinama visus elementus is saraso; kiekvienam saraso elementui dlist_destroy() kviecia atminties  
 *  atlaisvinimo funkcija perduota kaip destroy inicijuojant sarasa. (zinoma, jei destroy nera NULL) 
 *  Po sios funkcijos iskvietimo saraso naudoti nebegalima.
 */
void dlist_destroy(DList *list);

/*
 *  I saraso pabaiga iterpiamas naujas elementas su rodykle i data
 */
int dlist_insert(DList *list, const void *data);

/*
 *  Pasalina elementa, nurodyta parametru element, is saraso. 
 *  Po istrinimo rodykle data rodo i buvusio elemento duomenis.
 *  Grazina:
 *      0 - jei istrinimas pavyka
 *      -1 - kitu atveju
 */
int dlist_remove(DList *list, listElement *element, void **data);

int dlist_remove_by_data(DList *list, void *lookup, void **data);

/*  
 *  Surusiuoja saraso elementus didejimo tvarka.
 *  Elementams lyginti naudojama compare funkcija, nurodyta saraso inicijavime.
 *  
 */
void dlist_qsort(DList *list, int left, int right);

#endif
