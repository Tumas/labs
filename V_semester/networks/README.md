# SPELLcast

*Audio streaming server similar to Shoutcast and Icecast developed for computer network class. The intent of this project is to learn about computer networks, not to create another Icecast clone that could be used in real life situations.

## FEATURES:
  * supports mp3 file streaming 
  * ICY protocol
  * multiple broadcasts (sources) using mountpoints

## Using vlc as source 

    cvlc -vvv ~/Desktop/Dimdoz_JungleXSelectaH_VibeSteppazVol6.mp3 --sout '#transcode{acodec=mp3, ab=96, channels=2, samplerate=44100}:std{access=shout{mp3=1,bitrate=96, samplerate=44100, channels=2,name='name',genre='all'}, mux=raw, dst=source:vlcas@127.0.0.1:8001/test1}'

 streams file Dimdoz_JungleXSelectaH_VibeSteppazVol6.mp3 to SPELLcast (or any other *cast server) located on localhost on port 8001 using 'test1' as a mountpoint

    
## REFERENCES:

#### General concepts:
  * http://en.flossmanuals.net/Icecast/Introduction

#### Protocol:
  * http://forums.radiotoolbox.com/viewtopic.php?f=8&t=74
  * http://www.gigamonkeys.com/book/practical-a-shoutcast-server.html
  * http://www.smackfu.com/stuff/programming/shoutcast.html

#### Similar projects:
  * http://www.icecast.org/
  * http://ample.sourceforge.net/index.shtml
  * http://cs-people.bu.edu/liulk/slurpcast/

#### VLC:
  * http://www.videolan.org/doc/streaming-howto/en/

## ISSUES

#### C version
* mysterious bugs in server
* client doesnt do anything at all

#### Java version
* client consumes lots of resources (due to ArrayBlockingQueue probably)
* Server logging is a bit of a mess
