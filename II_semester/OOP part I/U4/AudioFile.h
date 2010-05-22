//AudioFile.h - Declaration of AudioFile class
#ifndef AUDIO_FILE
#define AUDIO_FILE

#include "DigitalFile.h"

class AudioFile : public DigitalFile
{
    protected:

        int duration;   //failo trukme sekundemis
        char *title;    //pavadinimas
        char *format;   //formatas
        float rating;   //vartotojo reitingas nuo 0 iki 5
        bool canBeShared; //ar galima faila laisvai platinti

        void invertPolicy();    //pakeicia failo platinimo galimybe: jei buvo true, tai pakeicia i false ir atvirksciai

    public:
        
        AudioFile();
        AudioFile(const AudioFile& other);  
        virtual ~AudioFile();

        const AudioFile& operator = (const AudioFile &other);  
        bool operator == (const AudioFile &other);
        
        void* operator new (size_t size);
        void operator delete (void* p);
       
        //Metodai
        virtual void display();             //spaudinti duomenis i console 
        virtual void editDataFromConsole(); //redaguoti duomenis pagal pageidavima
        virtual void fillDataFromConsole(); //uzpildyti visus objekto duomenu laukus   
        
        virtual void setTitle(const char* title);
        virtual void setFormat(const char *format);
        virtual void setDuration(int timeSeconds);
        virtual void setRating(float rating);
        virtual void setPolicy(bool canBeShared);

        virtual int getDuration() const;
        virtual int getMinutes() const;
        virtual int getSeconds() const;
        virtual float getRating() const;
        virtual bool getPolicy() const;
        virtual char *getTitle() const;
        virtual char *getFormat() const;
};

#endif
