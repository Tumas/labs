#include <iostream>
#include <cstring>

#include "Song.h"

using namespace std;

Song::Song() : genre(NULL), artist(NULL)
{
}


Song::~Song()
{
    if (getGenre() != NULL)
        delete [] getGenre();
    if (getArtist() != NULL)
        delete [] getArtist();
}

Song::Song(const Song& other) : AudioFile(other), genre(NULL), artist(NULL)
{
    setGenre(other.getGenre());
    setArtist(other.getArtist());
}
const Song& Song::operator= (const Song& other)
{
    //pasiziurim ar sau neprilyginam
    if (&other == this)
        return (*this);

    //sulyginam tevine dali
    AudioFile::operator=(other);
    //kopijuojam laukus
    setGenre(other.getGenre());
    setArtist(other.getArtist());
    
    return (*this);
}

void Song::setGenre(const char *pgenre)
{
    if (getGenre() != NULL)
        delete [] getGenre();
    if (pgenre != NULL){
        this->genre = new char [strlen(pgenre) + 1];
        strcpy(this->genre, pgenre);
    }
    else
        this->genre = NULL;
       
};

void Song::setArtist(const char *newartist)
{
    if (getArtist() != NULL)
        delete [] getArtist();
    if (newartist != NULL){
        this->artist = new char [ strlen(newartist) + 1];
        strcpy(this->artist, newartist);
    }
    else
        this->artist = NULL;
};

char *Song::getGenre() const { return genre; }
char *Song::getArtist() const { return artist; }

void Song::display()
{
    AudioFile::display();
    cout << "Autorius:     " << (getArtist() == NULL ? "Nenustatytas" : getArtist()) << endl;
    cout << "Stilius :     " << (getGenre() == NULL ? "Nenustatytas" : getGenre()) << endl;
    cout << endl << endl;
}

void Song::fillDataFromConsole() //uzpildyti visus objekto duomenu laukus   
{
    cout << "Uzpildykite informacija apie pasirinkta daina (toliau failas)" << endl;
    AudioFile::fillDataFromConsole();
    char in[256];
    cin.ignore();

    cout << "Iveskite failo Autoriu" << endl;
    cin.getline(in, 256);
    setArtist(in);
    
    cout << "Iveskite stiliaus pavadinima" << endl;
    cin.getline(in, 256);
    setGenre(in);
}

void Song::editDataFromConsole()
{
    AudioFile::editDataFromConsole();
    
    char in[256];

    cout << "Ar keisite autoriu/atlikeja? y/n";
    cin.getline(in, 256);
    if (strcmp(in, "y") == 0){
        cout << "Iveskite autoriu/atlikeja" << endl;
        cin.getline(in, 256);
        setArtist(in);
    }
    
    cout << "Ar keisite stiliu? y/n";
    cin.getline(in, 256);
    if (strcmp(in, "y") == 0){
        cout << "Iveskite nauja kurinio stiliu" << endl;
        cin.getline(in, 256);
        setGenre(in);
    }
}
