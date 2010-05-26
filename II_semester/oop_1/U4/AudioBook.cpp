#include <iostream>
#include <cstring>
#include <vector>

#include "AudioBook.h"
#include "Song.h"

using namespace std;

AudioBook::AudioBook() : author(NULL) {}
AudioBook::AudioBook(const AudioBook& other) : AudioFile(other), author(NULL)
{
    Song *ptr;
    setAuthor(other.getAuthor());

    for (unsigned int i = 0; i != other.toc.size(); i++)
    {
        ptr = new Song(*(other.toc[i]));
        toc.push_back(ptr); 
    }
}

const AudioBook& AudioBook::operator=(const AudioBook& other)
{
    if (&other == this)
        return (*this);

    AudioFile::operator=(other);

    Song *ptr;
    setAuthor(other.getAuthor());

    for (unsigned int i = 0; i != other.toc.size(); i++)
    {
        ptr = new Song(*(other.toc[i]));
        toc.push_back(ptr); 
    }
    return (*this);
}

AudioBook::~AudioBook()
{
    if (getAuthor() != NULL)
        delete [] author;
    for (unsigned int i = 0; i < toc.size(); i++)
        delete toc[i];
}

void AudioBook::appendChapter()
{
    Song* ptr = new Song();
    cout << "Pasirinkote prideti nauja skyriu! " << endl;
    cout << "Audio knyga sudaro skyriai : muzikiniai(dainos)/garso failai " << endl;
    ptr->fillDataFromConsole();
    toc.push_back(ptr);
}

void AudioBook::showTOC()
{
    if (toc.size() != 0)
        cout << "Turinys: " << endl; 
    for (unsigned int i = 0; i != toc.size(); i++){
        cout << i+1 << "  skyrius ........................ " << (toc[i]->getTitle() == NULL ? "Be pavadinimo " : toc[i]->getTitle()) <<
            " Autorius :  " << (toc[i]->getArtist() == NULL ? " Nezinomas " : toc[i]->getArtist()) << endl;
    }
}
char *AudioBook::getAuthor() const { return author; }
void AudioBook::setAuthor(const char* newauthor)
{
   if (getAuthor() != NULL)
       delete [] getAuthor();
   if (newauthor != NULL){
        this->author = new char [strlen(newauthor) + 1];
        strcpy(this->author, newauthor);
   }
}

void AudioBook::display()             //spaudinti duomenis i console 
{
    cout << "AudioKnyga : " << (getAuthor() == NULL ? " Nezinomas autorius " : getAuthor()) << (getTitle() == NULL ? " Nezinomas pavadinimas" : getTitle()) << endl; 
    showTOC();
    cout << endl;
    AudioFile::display();
}

void AudioBook::editDataFromConsole() //redaguoti duomenis pagal pageidavima
{
    AudioFile::editDataFromConsole();
    
    char in[256];

    cout << "Ar keisite knygos autoriu? y/n";
    cin.getline(in, 256);
    if (strcmp(in, "y") == 0){
        cout << "Iveskite knygos autoriu" << endl;
        cin.getline(in, 256);
        setAuthor(in);
    }
   
    do
    {
        cout << "Ar keisite turini? y/n";
        cin.getline(in, 256);
        editTOC();
    } while (strcmp(in, "y") == 0);
}

void AudioBook::fillDataFromConsole() //uzpildyti visus objekto duomenu laukus   
{
    cout << "Uzpildykite informacija apie pasirinkta audio knyga (toliau failas)" << endl;
    AudioFile::fillDataFromConsole();
    cin.ignore();
    char in[256];

    cout << "Iveskite knygos autoriu" << endl;
    cin.getline(in, 256);
    setAuthor(in);
    
    do
    {
        editTOC();
        cout << "Ar dar keisite turini? y/n" << endl;
        cin.getline(in, 256);
    }
    while (strcmp(in, "y") == 0);
}

void AudioBook::editTOC()
{
    char in[256];
    cout << "Ar pridesite, ar panaikinsite skyriu, ar nedarysite nieko? pri/pan/nieko" << endl;
    cin.getline(in, 256);

    if (strcmp(in, "pri") == 0)
    {
        appendChapter();
    }
    else if (strcmp(in, "pan") == 0)
    {
        unsigned int i;
        cout << "Koki skyriu pagal numeri norite panaikinti? (iveskite numeri)" << endl;
        cin >> i;
        if (i > toc.size())
            cout << "Klaida: Tokio skyriaus dar nera!" << endl;
        else {
            toc.erase(toc.begin() + i - 1);
            cout << "Skyrius panaikintas!" << endl;
        }
    }
    else if (strcmp(in, "nieko") == 0)
        cout << "Nepamirskite to padaryti veliau!" << endl;
    else
        cout << "Neatpazintas pasirinkimas ! Teks bandyti dar karta" << endl; 
    cin.ignore();
}
