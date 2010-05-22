#include <iostream>
#include <cstring>
#include <cstdio>
#include <cstdlib>

#include "AudioFile.h"

using namespace std;

AudioFile::AudioFile() 
{
    format = NULL;
    title = NULL;
    canBeShared = false;
    duration = 0;
    rating = 0.0;
}

AudioFile::AudioFile(const AudioFile &other)
{
    this->title = NULL;
    this->format = NULL;

    setTitle(other.getTitle());
    setFormat(other.getFormat());
    this->canBeShared = other.getPolicy();
    this->rating = other.getRating();
    this->duration = other.getDuration();
}

AudioFile::~AudioFile()
{
    if (getTitle() != NULL)
        delete [] title;
    if (getFormat() != NULL)
        delete [] format;
}

const AudioFile& AudioFile::operator = (const AudioFile &other)
{
    //tiesiogiai nenaudojam, nes gali buti kad jau yra iskirta atmintis, kuria reikia atlaisvinti
    setTitle(other.getTitle());
    setFormat(other.getFormat());
    setPolicy(other.getPolicy());
    setRating(other.getRating());
    setDuration(other.getDuration());
    return *this;
}

void* AudioFile::operator new (size_t size)
{
    void *p = malloc(size);
    if (p == NULL){
        cout << "Nepavyko issiskirti atminties!" << endl;
    }
    return p;
}

void AudioFile::operator delete(void *file)
{
    AudioFile *ptr = static_cast<AudioFile*>(file);
    free(ptr);
}

bool AudioFile::operator == (const AudioFile &other)
{
    if (getTitle() == NULL || other.getTitle() == NULL)
        return false;
    else
        return (strcmp(getTitle(), other.getTitle()) == 0 ? true : false);
}
void AudioFile::setTitle(const char *title)
{
    if (getTitle() != NULL)
        delete [] getTitle();

    if (title != NULL){
        this->title = new char [strlen(title)+1];
        strcpy(this->title, title);
    }
    else
        this->title = NULL;
}

void AudioFile::setFormat(const char *format)
{
    if (getFormat() != NULL)
        delete [] getFormat();

    if (format != NULL){
        this->format = new char [strlen(format)+1];
        strcpy(this->format, format);
    }
    else
        this->format = NULL;
}
void AudioFile::setRating(float rating)
{
    this->rating = ((rating >= 0 && rating <= 5) ? rating : 0);
}
void AudioFile::setPolicy(bool canBeShared)
{
    this->canBeShared = canBeShared;
}

void AudioFile::setDuration(int timeSeconds)
{
    this->duration = timeSeconds;
}

int AudioFile::getMinutes() const { return duration / 60; }
int AudioFile::getSeconds() const { return duration % 60; }
int AudioFile::getDuration() const { return duration; }
float AudioFile::getRating() const { return rating; }
bool AudioFile::getPolicy() const  { return canBeShared; }
char *AudioFile::getTitle() const { return title; }
char *AudioFile::getFormat() const { return format; }

void AudioFile::invertPolicy()
{
    if (getPolicy())
        setPolicy(false);
    else
        setPolicy(true);
}
void AudioFile::display()
{
    cout << "-------------------------------------------------" << endl;
    cout << "Failas:           "<< (getTitle() == NULL ? " nenustatytas " : getTitle() ) << endl;
    cout << "Formatas:         " << (getFormat() == NULL ? " nenustatytas " : getFormat()) << endl;
    cout << "Trukme:           " << getDuration() << " sek." << endl;
    cout << "Vartotojo suteiktas reitingas: " << getRating() << endl;
    cout << "Failo platinimas " << (getPolicy() ? "leidziamas" : "neleidziamas") << endl;
    cout << endl;
}

void AudioFile::editDataFromConsole()
{
    char in[256];

    cout << "Ar keisite pavadinima? y/n";
    cin.getline(in, 256);
    if (strcmp(in, "y") == 0){
        cout << "Iveskite failo pavadinima" << endl;
        cin.getline(in, 256);
        setTitle(in);
    }
    
    cout << "Ar keisite formata? y/n";
    cin.getline(in, 256);
    if (strcmp(in, "y") == 0){
        cout << "Iveskite failo formata" << endl;
        cin.getline(in, 256);
        setFormat(in);
    }

    cout << "Ar keisite reitinga? y/n" << endl;
    cin.getline(in, 256);
    if (strcmp(in, "y") == 0){
        cout << "Koki reitinga failui suteiksite? [0 .. 5]" << endl;
        cin.getline(in, 256);
        setRating((float)atof(in));
    }

    cout << "Ar keisite Licenzija? y/n" << endl;
    cin.getline(in, 256);
    if (strcmp(in, "y") == 0){
        invertPolicy();
        cout << "Licenzija pakeista!" << endl;
    }
    
    cout << "Ar keisite failo trukme? y/n";
    cin.getline(in, 256);
    if (strcmp(in, "y") == 0){
        cout << "Iveskite failo ilgi sekundemis" << endl;
        cin.getline(in, 256);
        setDuration(atoi(in));
    }
}
void AudioFile::fillDataFromConsole()
{
    char in[256];
    char c;

    cout << "Iveskite failo pavadinima" << endl;
    cin.getline(in, 256);
    setTitle(in);
    
    cout << "Iveskite failo formata" << endl;
    cin.getline(in, 256);
    setFormat(in);
    
    cout << "Iveskite failo ilgi sekundemis" << endl;
    cin.getline(in, 256);
    setDuration(atoi(in));
    
    cout << "Koki reitinga failui suteiksite? [0 .. 5]" << endl;
    cin.getline(in, 256);
    setRating((float)atof(in));

    cout << "Ar faila galima laisvai platinti? [y/n]" << endl;
    cin >> c;
    setPolicy((c == 'y' ? true : false)); 
}
