#ifndef DIGITALFILECLASS
#define DIGITALFILECLASS


/*  Abstrakti klase : nera implementacijos, nera data member'iu */
class DigitalFile
{
    public:
        DigitalFile(){};
        virtual ~DigitalFile(){};

        virtual void display() = 0;             //spaudinti duomenis i console 
        virtual void editDataFromConsole() = 0; //redaguoti duomenis pagal pageidavima
        virtual void fillDataFromConsole() = 0; //uzpildyti visus objekto duomenu laukus
};

#endif
