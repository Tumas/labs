package psi.observer;

import java.util.HashMap;
import java.util.Map;

import psi.observer.assets.Song;
import psi.observer.commands.music.AddSongCommand;
import psi.observer.commands.music.PlaySongCommand;
import psi.observer.commands.music.StopSongCommand;
import psi.observer.musicServer.MusicServer;
import psi.observer.views.CurrentStatusMediaView;

public class Main {
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		
		Song s1 = new Song("Song1", "Unknown artist", 2500);
		Song s2 = new Song("Song2", "Unknown artist", 2200);
		
		//Video v1 = new Video("Funny video #1", 123343);
		//Video v2 = new Video("Funny video #2", 12344);

		MusicServer ms = new MusicServer();
		ms.addObserver(new CurrentStatusMediaView());

		//VideoServer vs = new VideoServer();
		
		Map eventsAndActions = new HashMap<String, Command[]>();
		eventsAndActions.put("PLAY", new Command[]{ new PlaySongCommand(ms) });
		eventsAndActions.put("ADD", new Command[]{ new AddSongCommand(ms, s1), new AddSongCommand(ms, s2) });

		Controller c = new Controller(eventsAndActions);
		
		c.eventPressed("ADD");
		c.eventPressed("PLAY");
		c.undo();
	}
}
