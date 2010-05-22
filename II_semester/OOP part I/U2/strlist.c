#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "list.h"
#include "strlist.h"

/*
 *  Funkcija naudojama dviem simboliu eilutems lyginti.
 *  Priesingai nei strcmp(), my_strcmp() vienodai interpretuoja didziasias ir mazasias raides.
 *
 */
static int my_strcmp(const char *a, const char *b)
{
    while (tolower((unsigned char)*a) == tolower((unsigned char) *b))
    {
        if (*a == '\0') return 0;
        a++;
        b++;
    }
    return tolower((unsigned char)*a) < tolower((unsigned char)*b) ? -1 : +1;
}

int compare_strings(void *key1, void *key2)
{
    return my_strcmp((const char*)key1, (const char*)key2);
}


void print_list(FILE *stream, const DList *list)
{
    listElement *temp = dlist_head(list);
    while (temp != NULL){
        fprintf(stream, "%s\n", (char*)dlist_data(temp));
        temp = temp->next;
    }
}

int get_strings(DList *list1, DList *list2, char *search_combo, char *filename)
{
    FILE *fin;
    char buffer[MAX_LEN];
    char *ptr, *word;

    //atsiveriam duomenu faila
    if ((fin = fopen(filename, "r")) == NULL){
        printf("Klaida: nepavyko atverti duomenu failo.\n");
        return(-1);
    }   

    //skaitom duomenis is duomenu failo ir formuojam sarasus
    while (fgets(buffer, MAX_LEN, fin) != NULL){
        //skaidom simbolius i zodzius
        word = strtok(buffer, " .,:()?!\"\n\t");
        while (word != NULL){
            //pasidedam zodi i heap'a
            if ((ptr = (char*) malloc(strlen(word)+1)) == NULL){    //sizeof neveikia teisingai
                printf("Nebeuztenka atminties!\n");
                return(-1);
            }
            memcpy(ptr, word, strlen(word)+1);
            //issisaugom zodzio adresa kuriam nors sarase
            if (strstr(word, search_combo) != NULL){
                dlist_insert(list1, ptr);
            }
            else {
                dlist_insert(list2, ptr);
            }
            word = strtok(NULL, " .,:()?!\"\n\t");
        }
    }
    
    fclose(fin);
    return(0);
}

void printIntro()
{
    printf("*****************************************\n");
    printf("OOP U2 programa #50. Autorius: Tumas Bajoras PS3\n");
    printf("Nuskaityti viena faila. Sukurti du dvipusius sarasus, kuriu elementai eilutes.\n");
    printf("I pirma patalpinti zodzius, kuriuose galima rasti nurodyta dvieju simboliu kombinacija, i antra - kitus zodzius.\n");
    printf("Juos surusiuoti pagal abecele. Rezultatus isvesti i atskirus failus.\n");
}

void printOutro(char *name1, char *name2)
{
    printf("\nProgramos darbo rezultatus galite perziureti %s ir %s failuose.\n", name1, name2);
}
