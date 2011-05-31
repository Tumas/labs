package psi.observer.commands.music;

import psi.observer.Command;
import psi.observer.assets.Song;
import psi.observer.commands.UndoableCommandException;
import psi.observer.musicServer.MusicServer;

public class AddSongCommand implements Command {
	private MusicServer m;
	private Song s;
	
	public AddSongCommand(MusicServer ms, Song s){
		this.m = ms;
		this.s = s;
	}

	@Override
	public void execute() {
		m.addSongToQueue(s);
	}

	@Override
	public void undo() throws UndoableCommandException {
		throw new UndoableCommandException();
	}
}