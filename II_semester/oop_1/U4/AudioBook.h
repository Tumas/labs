#ifndef MYAUDIOBOOK
#define MYAUDIOBOOK

#include <vector>
#include "AudioFile.h"
#include "Song.h"

using std::vector;

class AudioBook : public AudioFile
{
    protected:

        char *author;
        vector<Song*> toc;          //AudioBook'as yra sudarytas is Dainu - Dainos atitinka kiekviena knygos skyriu. Skyrius negali priklausyti kitai knygai
                                    //o sunaikinus Knyga - Skyriai dingsta  : Kompozicija
                                    //Daina turi papildomus data memberius : autoriu ir stiliu : tai gali buti panaudota jei AudioBook'as yra sudarytas is skirtingu
                                    //autoriu darbu rinkiniu
    public:
        AudioBook();
        AudioBook(const AudioBook& other);
        const AudioBook& operator=(const AudioBook& other);
        virtual ~AudioBook();
        
        //unikalus AudioBook'o metodai
        virtual void showTOC();
        virtual void editTOC();
        virtual void appendChapter();
        //-- 
        
        virtual void setAuthor(const char* newauthor);
        virtual char *getAuthor() const;

        virtual void display();             //spaudinti duomenis i console 
        virtual void editDataFromConsole(); //redaguoti duomenis pagal pageidavima
        virtual void fillDataFromConsole(); //uzpildyti visus objekto duomenu laukus   
};

#endif
