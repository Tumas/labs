#ifndef U1_OOP
#define U1_OOP

int binarySearch(int *, int, int, int);

//tas pats kaip ir binarySearch tik taikomas mazejanciam masyvui
int binarySearch2(int *, int, int, int);

//Funkcija kuri pagal duota trecia parametra nustato ar antras parametras palaiko didejimo arba mazejimo tvarka
int testNumber(int, int, int);

//funkcija, kuri spaudina masyva
//Parametrai: rodykle i masyvo pradzia ir masyvo dydis
void outputArray(int *, int);

//funkcija atliekanti dinamines atminties perskirstyma + patikrina ar perskirstynas pavyko
//grazina 0  jei viskas gerai ir -1 klaidos atveju
int getAndCheck(int, int **);

//Formuoja sveiku skaiciu dinamini masyva ir grazina masyvo elementu skaiciu
//Parametrai: rodykle i rodykle i masyvo pradzia ir rodykle i Didejimo/mazejimo pozymio kintamaji
int readData(int **, int *);

#endif
