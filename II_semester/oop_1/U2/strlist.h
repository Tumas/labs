#ifndef STRLIST
#define STRLIST
#define MAX_LEN 256


/*
 *  Funkcijos darbui su saraso elementais - simboliu eilutemis.
 *  MAX_LEN - maksimalus eilutes ilgis
 *  
 */

/*
 *  Funkcija dviem simboliu eilutem lyginti
 */
int compare_strings(void *key1, void *key2);

/*
 *  Funkcija spausdina saraso elementus eiles tvarka i nurodyta srauta
 */
void print_list(FILE* stream, const DList *list);

/*
 *  Funkcija skaito filename duomenu faila ir formuoja du sarasus.
 *      Vieno saraso elementai - eilutes, kuriose yra dvieju simboliu search_combo kombinacija
 *      Kito saraso elementai - eilutes, kuriose nera dvieju simboliu search_combo kombinacijos
 *
 */
int get_strings(DList *list1, DList *list2, char *search_combo, char *filename);

/*
 *  Pagalbines funkcijos, isvedancios informacija apie programa 
 */
void printIntro();
void printOutro(char *name1, char *name2);

#endif
