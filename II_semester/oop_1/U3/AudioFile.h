//AudioFile.h - Declaration of AudioFile class
#ifndef AUDIO_FILE
#define AUDIO_FILE

class AudioFile
{
    private:
        int duration;   //failo trukme sekundemis
        char *title;    //pavadinimas
        char *format;   //formatas
        float rating;   //vartotojo reitingas nuo 0 iki 5
        bool canBeShared; //ar galima faila laisvai platinti

        void invertPolicy();    //pakeicia failo platinimo galimybe

    public:
        AudioFile();
        AudioFile(const AudioFile& other);  
        ~AudioFile();

        const AudioFile& operator = (const AudioFile &other);  
        bool operator == (const AudioFile &other);

        void* operator new (size_t size);
        void operator delete (void* p);
       
        //Metodai
        void display();             //spaudinti duomenis i console 
        void editDataFromConsole(); //redaguoti duomenis pagal pageidavima
        void fillDataFromConsole(); //uzpildyti visus objekto duomenu laukus   
        
        //accessors and mutators
        void setTitle(const char* title);
        void setFormat(const char *format);
        void setDuration(int timeSeconds);
        void setRating(float rating);
        void setPolicy(bool canBeShared);

        int getDuration() const;
        int getMinutes() const;
        int getSeconds() const;
        float getRating() const;
        bool getPolicy() const;
        char *getTitle() const;
        char *getFormat() const;
};

#endif
