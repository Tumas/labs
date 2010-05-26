#include <iostream>
#include <cstring>
#include <cstdlib>
#include <vector>

/*    OOP U4:  
 *      Pati auksciausia klase hierarchijoe yra abstrakti DigitalFile klase. U3 uzduoties klase AudioFile paveldi is DigitalFile, o klases Song ir AudioBook
 *      paveldi is AudioFile. 
 */

#include "DigitalFile.h"
#include "AudioFile.h"
#include "Song.h"
#include "AudioBook.h"

using namespace std;

void printMenu(vector<DigitalFile*> &demo)
{
    cout << " ---------- Veiksmai su garso iraso failais ------------- Sukurtu objektu skaicius: "<< demo.size() << endl;
    cout << " 1 - sukurti nauja daina (Kontroliuosite naujai sukurta objekta) " << endl;
    cout << " 2 - sukurti nauja audio knyga (Kontroliuosite naujai sukurta objekta) " << endl;
    cout << " 3 - prideti duomenis " << endl;
    cout << " 4 - keisti duomenis " << endl;
    cout << " 5 - spausdinti informacija apie faila " << endl;
    cout << " 6 - keisti pasirinktaji objekta " << endl;
    cout << " 7 - prideti skyriu (tik audio knygom!) " << endl;
    cout << " 9 - baigti programa " << endl;
}

int main()
{
    cout << "******** OOP U4 ********** Autorius: Tumas Bajoras PS3 " << endl;
    cout << "*** Programoje dinamiskai kuriami AudioBook arba Song klasiu objektai" << endl;
    cout << endl; 

    vector<DigitalFile*> demo;
    DigitalFile* current = NULL, *temp = NULL;

    int test;
    unsigned int test2;
    char in[256];

    do {
        printMenu(demo);
        cin.getline(in, 256);
        test = atoi(in);

        switch (test){
            case 1: 
                temp = new Song(); 
                demo.push_back(temp);
                current = temp;
                cout << "Nauja daina sukurta!" << endl;
                break;
            case 2:
                temp = new AudioBook(); 
                demo.push_back(temp);
                current = temp;
                cout << "Nauja audio knyga sukurta!" << endl;
                break;
            case 3: 
                if (current != NULL)
                    current->fillDataFromConsole();
                else
                    cout << "Pirma sukurkite objekta!" << endl;
                break;
            case 4: 
                if (current != NULL)
                    current->editDataFromConsole();
                else
                    cout << "Pirma sukurkite objekta!" << endl;
                break;
            case 5: 
                if (current != NULL)
                    current->display();
                else
                    cout << "Pirma sukurkite objekta!" << endl;
                break;
            case 6: 
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
            case 7:
                try {
                    if (current == NULL)
                        throw (-1);
                    AudioBook& bookref = dynamic_cast<AudioBook&>(*current);
                    bookref.appendChapter();
                    cout << "Sekmingai pridejote nauja skyriu!" << endl;
                } catch (exception bad_cast){
                    cout << "Klaida! Skyriu prideti galima tik prie audio knygos! " << endl;
                }
                catch(int ex){
                    cout << "Pirma sukurkite objekta!" << endl;
                }
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
