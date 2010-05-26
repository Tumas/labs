#include <stdio.h>
#include <stdlib.h>
#include "u1_oop.h"

/*
 *  U1 uzduotis #30:
 *      Ivedamas surusiuotas masyvas ir ieskomas skaicius. Realizuoti dvejetaine paieska ir
 *      isvesti i ekrana pradini masyva, ieskoma reiksme ir masyvo indeksa, jei reiksme rasta.
 *      reiksme masyve negali kartotis.
 *
 *      Autorius: Tumas Bajoras PS3
 */

int main()
{

    int *numbers = NULL;    //rodykle i masyvo pradzia
    int howMany = 0; //kiek masyve yra skaiciu
    int isIncreasing = 0;    //Ar masyvas surusiuotas didejimo tvarka?
    int searchValue = 0;     //ieskoma reiksmes

    printf("OOP_U1, #30: Realizuoti dvejetaine paieska ivestam surusiuotam masyvui\n");
    printf("Klaviatura iveskite sveiku skaiciu seka po kiekvieno skaiciaus paspausdami \"Enter\"\n");
    printf("DEMESIO!\n");
    printf("\t1) Skaiciai turi buti ivedami surusiuota tvarka\n");
    printf("\t2) Skaiciai negali kartotis\n");
    printf("\t3) Noredami baigti ivedima iveskite 1 arba 2 punkto neatitinkanti skaiciu\n");

    howMany = readData(&numbers, &isIncreasing);

    if (howMany == 0){
        return (-1);
    }

    printf("Iveskite skaiciu, kurio ieskosime\n");
    scanf("%d", &searchValue);

    printf("Pradinis masyvas:\t");
    outputArray(numbers, howMany);
  
    int index;
    if (isIncreasing == 1)
        index = binarySearch(numbers, searchValue, 0, howMany-1);
    else
        index = binarySearch2(numbers, searchValue, 0, howMany-1);

    printf("Ieskoma reiksme:\t%d\n", searchValue);
    if (index != -1)
        printf("Ieskomos reiksmes indeksas masyve yra %d\n", index);
    else
        printf("Reiksme masyve nerasta\n");
    
    return(0);
}

//grazinama reiksme: nuskaitytu elementu i masyva skaicius 
int readData(int **ptr, int *isIncreasing)
{

    int num, num1;  
    int count = 0;

    int test = scanf("%d", &num);       //jei nuskaite tai ka norejo - 1, jei klaida : 0

    if (test == 0){
        printf("Nuskaityta netaisyklinga reiksme. Masyvas tuscias!\n");
        return(0);
    }
    //isirasom pirma skaiciu
    count++;
    if (getAndCheck(count, ptr) == -1)
        return(0);
    **ptr = num;

    test = scanf("%d", &num1);
    if (test == 0 || num == num1){
        return(1);
    }
    //isirasom antra skaiciu
    count++;
    if (getAndCheck(count, ptr) == -1)
        return(0);
    *(*ptr+count-1) = num1;
    //nusistatom masyvo rikiavimo tvarka 
    *isIncreasing = num > num1 ? 0 : 1;
    
    //skaitom likusius skaicius
    while (scanf("%d", &num) && testNumber(*(*ptr+count-1), num, *isIncreasing)){
        count++;
        if (getAndCheck(count, ptr) == -1)
            return(0);
        *(*ptr+count-1) = num;
    };
    
    return(count);
}

//funkcija atlieka dinamines atminties perorganizavima + patikrinimas
int getAndCheck(int kiek, int **ptr){
    *ptr = (int*) realloc(*ptr, kiek*sizeof(int));
    
    if (*ptr == NULL){
        printf("Nepavyko issiskirti dinamines atminties: realloc()\n");
        return -1;
    }
    return 0;
}
//Funkcija kuri isspausdina dinamini masyva
void outputArray(int *ptr, int size)
{
    int i;
    for (i = 0; i < size; i++)
        printf("%d ", *(ptr+i));
    printf("\n");
}

int binarySearch(int *array, int searchValue, int lowIndex, int highIndex)
{
    if (highIndex < lowIndex)
        return(-1);     //reiksme nerasta
    int midIndex = lowIndex + ((highIndex - lowIndex) / 2);

    if (*(array + midIndex) > searchValue)
        return binarySearch(array, searchValue, lowIndex, midIndex-1);
    else if (*(array + midIndex) < searchValue)
        return binarySearch(array, searchValue, midIndex+1, highIndex);
    else
        return midIndex;
}
//tas pats tik naudojamas jei masyvas surusiuotas mazejimo tvarka
int binarySearch2(int *array, int searchValue, int lowIndex, int highIndex)
{
    if (highIndex < lowIndex)
        return(-1);     //reiksme nerasta
    int midIndex = lowIndex + ((highIndex - lowIndex) / 2);

    if (*(array + midIndex) < searchValue)
        return binarySearch(array, searchValue, lowIndex, midIndex-1);
    else if (*(array + midIndex) > searchValue)
        return binarySearch(array, searchValue, midIndex+1, highIndex);
    else
        return midIndex;
}

//Funkcija tikrina ar ivestas skaicius atitinka pasirinkta (didejimo arba mazejimo) tvarka
int testNumber(int previous, int current, int order)
{
    if (order)  //1 - didejimo
        return previous < current ? 1 : 0;
    else
        return previous > current ? 1 : 0;
}
