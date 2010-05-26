#ifndef MYSONGCLASS
#define MYSONGCLASS

#include "AudioFile.h"

class Song : public AudioFile
{
    protected:

        char *genre;
        char *artist;

    public:
        Song();
        Song(const Song& other);
        const Song& operator= (const Song& other);
        virtual ~Song();
    
        virtual void display();             //spaudinti duomenis i console 
        virtual void editDataFromConsole(); //redaguoti duomenis pagal pageidavima
        virtual void fillDataFromConsole(); //uzpildyti visus objekto duomenu laukus   

        virtual void setGenre(const char *genre);
        virtual void setArtist(const char *newartist);
        virtual char *getGenre() const;
        virtual char *getArtist() const;
};


#endif
