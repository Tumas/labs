package psi.observer;

import psi.observer.assets.Song;
import psi.observer.assets.Video;
import psi.observer.commands.music.AddSongCommand;
import psi.observer.commands.music.PlaySongCommand;
import psi.observer.musicServer.MusicServer;
import psi.observer.videoServer.VideoServer;
import psi.observer.views.CurrentStatusMediaView;
import psi.observer.views.StatisticalMediaView;

public class Main {
	public static void main(String[] args){
		Controller c = new Controller();
		
		Song s1 = new Song("Song1", "Unknown artist", 2500);
		Song s2 = new Song("Song2", "Unknown artist", 2200);
		
		//Video v1 = new Video("Funny video #1", 123343);
		//Video v2 = new Video("Funny video #2", 12344);

		MusicServer ms = new MusicServer();
		//VideoServer vs = new VideoServer();

		//ms.addObserver(new StatisticalMediaView());
		ms.addObserver(new CurrentStatusMediaView());
		
		c.execute(new AddSongCommand(ms, s1));
		c.execute(new AddSongCommand(ms, s2));
		c.execute(new PlaySongCommand(ms));
		
		c.undo();
		c.undo();
	}
}
