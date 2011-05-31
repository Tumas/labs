package psi.observer.commands.music;

import psi.observer.Command;
import psi.observer.assets.Song;
import psi.observer.commands.UndoableCommandException;
import psi.observer.musicServer.MusicServer;

public class RemoveSongCommand implements Command {
	private MusicServer m;
	private Song s;
	
	public RemoveSongCommand(MusicServer ms, Song s){
		this.m = ms;
		this.s = s;
	}

	@Override
	public void execute() {
		m.removeFromQueue(s);
	}

	@Override
	public void undo() throws UndoableCommandException {
		throw new UndoableCommandException();
	}
}