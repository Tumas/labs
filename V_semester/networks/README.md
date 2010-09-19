# SHOUTCAST protokolas per TCP/IP (be HTTP):  saltinis + serveris + klientas 

## C  
* Portabilus kodas (Win, Unix aplinkos)
* Naudojami multipleksoriai (select arba poll)
* Serveris turi aptikti nenumatyta klientu atsijungima (atlaisvinti resursus ir testi darba)
* Turi kompiliuotis uosio serveryje, turi buti paruostas make failas
* multicast?

## Java
* Java nio?

### Shoutcast protokolas

#### Source (saltinis)

1. Saltinis prisijungia prie serverio 
2. Saltinis siuncia slaptazodi serveriui

    password\r\n

3.1 Jei slaptazodis geras serveris atsako:

  OK2\r\nicy-caps:11\r\n\r\n 

3.2 Jei slaptazodis blogas:

  invalid password\r\n

4. Saltinis, gaves atskaymas OK2 siuncia informacija apie stream'a serveriui:
  
    icy-name:Unnamed Server\r\n
    icy-genre:Unknown Genre\r\n
    icy-pub:1\r\n
    icy-br:56\r\n
    icy-url:http://www.shoutcast.com\r\n
    icy-irc:%23shoutcast\r\n
    icy-icq:0\r\n
    icy-aim:N%2FA\r\n
    \r\n

5. Saltinis siuncia mp3 uzkoduota srauta

### Serveris  (bus programuojamas)
* 2 rusiu socketai: vienas saltiniui, kitas klientam

### Klientas (bus programuojamas)

* Prisijungia prie serverio 
  - gauti tinkamu stociu sarasa
  - atsijungti 
  - prisijungti prie stoties su ID
  - atsijungit nuo stoties (two way communication?)

1. Klientas prisijungia prie serverio ir siuncia informacija apie save:

  CLIENT  ???

  SOURCE / HTTP/1.0
  Authorization: Basic c291cmNlOnZsY2Fz
  User-Agent: VLC media player 1.0.6
  Content-Type: audio/mpeg
  ice-name: name
  ice-public: 0
  ice-url: http://www.videolan.org/vlc
  ice-genre: all
  ice-audio-info: bitrate=96;samplerate=44100;channels=2
  ice-description: Live stream from VLC media player

1.1 Jei klientas nori gauti metadata apie mp3 stream'a siuncia papildoma lauka:

  icy-metadata:1\r\n

2. Serveris atsako:

  ICY 200 OK\r\n
  icy-notice1:<BR>This stream requires <a href="http://www.winamp.com/">Winamp</a><BR> 
  icy-notice2:SHOUTcast Distributed Network Audio Server/posix v1.x.x<BR> 
  icy-name:Unnamed Server\r\n 
  icy-genre:Unknown Genre\r\n 
  icy-url:http://www.shoutcast.com\r\n 
  Content-Type:audio/mpeg\r\n 
  icy-pub:1\r\n 
  icy-br:56\r\n
  icy-metaint:8192\r\n 
  \r\n (end of header)

3. Serveris persiuncia mp3 srauta

icy-metaint:8192\r\n siunciamas jei  icy-metadata:1 

#### SHOUTcast meta data information
