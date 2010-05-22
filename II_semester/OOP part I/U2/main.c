#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "list.h"
#include "strlist.h"

/*
 *  OOP2:   Nuskaityti viena faila. Sukurti du dvipusius sarasus, kuriu elementai eilutes. I pirmaji patalpinti zodzius, 
 *          kuriuose galima rasti nurodyta dvieju simboliu kombinacija, i antraji - kitus zodzius. Juos surusiuoti pagal 
 *          abecele. Rezultatus isvesti i atskirus failus.
 *          Uzduoties numeris: 50
 *
 *      Metodiniai reikalavimai:
 *          1. Sukurti multifailini projekta. Dinamines duomenu strukturas, ju apibrezimus, operacijas realizuoti atskirame faile
 *          2. Pradinius duomenis nuskaityti is vieno ar keliu tekstiniu failu 
 *          3. Isanalizuoti faila kaip nurodyta uzduotyje, informacija patalpinti i dinamines duomenu strukturas. Pastaba: kad atskirti 
 *          zodzius tekste, galima apibrezti skirtuku aibe
 *          4. Jei reikia, atlikti papildomas operacijas
 *          5. Rezultatus isvesti i faila(us).
 *          6. Pageidautina, kad ivedimo-isvedimo failu vardai butu perduodami programai kaip komandines eilutes parametrai 
 *          7. Programa turi buti apsaugota nuo nekorektisku parametru ar nuskaitomu duomenu - turi isvesti informatyvius klaidu pranesimus.
 *          8. Kompiliavimui naudojama MAKE programa
 *
 *          Autorius: Tumas Bajoras PS3 
 *
 *
 *          Komandines eilutes parametrai:  Dvieju simboliu kombinacija, Duomenu failas, Du isvedimo failai
 *
 */

int main(int argc, char* argv[])
{
    printIntro();
    /*  Tikriname ar ar tinkamas parametru skaicius ir ar tinkamai parametrai */
    if (argc != 5) {
        printf("Neteisingas parametru skaicius!\n\tParametrai: Dvieju simboliu kombinacija, duomenu failas, isvedimo failas 1, isvedimo failas2\n");
        return(-1);
    }
    if (strlen(argv[1]) != 2){
        printf("Klaida: Programos pirmasis parametras turi buti 2 simboliu kombinacija.\n\tNurodytas ilgis: %d\n", strlen(argv[1]));
        return(-1);
    }

    //rezultatu failai ir sarasai
    FILE *fout1, *fout2;
    DList first_list, second_list;

    //Susikuriam sarasus
    dlist_init(&first_list, free, compare_strings);
    dlist_init(&second_list, free, compare_strings);

    //nusiskaitom duomenis
    if (get_strings(&first_list, &second_list, argv[1], argv[2]) == -1)
        return(-1);

    /* TESTAVIMUI
    printf("pirmas sarasas: %d\n", dlist_size(&first_list));
    print_list(stdout, &first_list);
    printf("antras sarasas: %d\n", dlist_size(&second_list));
    print_list(stdout, &second_list);
    */

    dlist_qsort(&first_list, 0, dlist_size(&first_list)-1);
    dlist_qsort(&second_list, 0, dlist_size(&second_list)-1);
    
    /*
    printf("pirmas sarasas: %d\n", dlist_size(&first_list));
    print_list(stdout, &first_list);
    printf("antras sarasas: %d\n", dlist_size(&second_list));
    print_list(stdout, &second_list);
    */

    if ((fout1 = fopen(argv[3], "w")) != NULL && (fout2 = fopen(argv[4], "w")) != NULL){
        print_list(fout1, &first_list);
        print_list(fout2, &second_list);
        fclose(fout1);
        fclose(fout2);
        printOutro(argv[3], argv[4]);
    }
    else 
        printf("Klaida, nepavyko sukurti/atverti rezultatu failu\n");

    //graziname resursus
    dlist_destroy(&second_list);
    dlist_destroy(&first_list);

    return(0);
}
