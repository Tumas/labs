#include <iostream>
#include <cstring>
#include <cstdlib>
#include <vector>

#include "AudioFile.h"

using namespace std;

void printMenu(vector<AudioFile*> &demo)
{
    cout << " ---------- Veiksmai su garso iraso failu ------------- Sukurtu objektu skaicius: "<< demo.size() << endl;
    cout << " 1 - sukurti nauja faila (Kontroliuosite naujai sukurta objekta) " << endl;
    cout << " 2 - prideti duomenis " << endl;
    cout << " 3 - keisti duomenis " << endl;
    cout << " 4 - spausdinti informacija apie faila " << endl;
    cout << " 5 - keisti pasirinktaji objekta " << endl;
    cout << " 9 - baigti programa " << endl;
}

int main()
{
    cout << "******** OOP U3 ********** Autorius: Tumas Bajoras PS3 " << endl;
    cout << "*** Programoje dinamsikai kuriami skaitmeninio garso iraso klases objektai" << endl;
    cout << endl; 

    vector<AudioFile*> demo;
    AudioFile* current = NULL, *temp = NULL;

    int test;
    unsigned int test2;
    char in[256];

    do {
        printMenu(demo);
        cin.getline(in, 256);
        test = atoi(in);

        switch (test){
            case 1: 
                temp = new AudioFile; 
                demo.push_back(temp);
                current = temp;
                break;
            case 2: 
                if (current != NULL)
                    current->fillDataFromConsole();
                else
                    cout << "Pirma sukurkite objekta!" << endl;
                break;
            case 3: 
                if (current != NULL)
                    current->editDataFromConsole();
                else
                    cout << "Pirma sukurkite objekta!" << endl;
                break;
            case 4: 
                if (current != NULL)
                    current->display();
                else
                    cout << "Pirma sukurkite objekta!" << endl;
                break;
            case 5: 
                if (current != NULL){
                    cout << "Iveskite naujo objekto numeri!" << endl;
                    cin >> test2;
                    cin.ignore();

                    if (test2 < demo.size()){
                        current = demo[test2];
                        cout << "Kontroliuoje faila #" << test2 << endl; 
                    }
                    else
                        cout << "Toks objektas dar nesukurtas!" << endl;
                }
                else
                    cout << "Pirma sukurkite objekta!" << endl;
                break;
            case 9: break;
            default:
                cout << "Neatpazistamas pasirinkimas!" << endl;
                break;
        }
    } while (test != 9);

    for (unsigned int i = 0; i < demo.size(); i++)
        delete demo[i];
    return 0;
}
