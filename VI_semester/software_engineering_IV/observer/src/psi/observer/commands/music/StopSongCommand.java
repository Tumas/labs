package psi.observer.commands.music;

import psi.observer.Command;
import psi.observer.musicServer.MusicServer;
import psi.observer.musicServer.MusicServerMemento;

public class StopSongCommand implements Command {

	private MusicServer m;
	private MusicServerMemento memento;
	
	public StopSongCommand(MusicServer m){
		this.m = m;
	}
	
	@Override
	public void execute() {
		memento = m.createMemento();
		m.stop();
	}

	@Override
	public void undo() {
		if (memento != null) m.setMemento(memento);
	}
}
