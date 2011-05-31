package psi.observer.commands.music;

import psi.observer.Command;
import psi.observer.musicServer.MusicServer;
import psi.observer.musicServer.MusicServerMemento;

public class PlaySongCommand implements Command {

	private psi.observer.musicServer.MusicServer m;
	private MusicServerMemento memento;
	
	public PlaySongCommand(MusicServer m){
		this.m = m;
	}
	
	@Override
	public void execute() {
		memento = m.createMemento();
		m.play();
	}

	@Override
	public void undo() {
		if (memento != null) m.setMemento(memento);
	}
}
