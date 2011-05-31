package psi.observer.commands.video;

import psi.observer.Command;
import psi.observer.videoServer.VideoServer;
import psi.observer.videoServer.VideoServerMemento;

public class PlayVideoCommand implements Command {
	private VideoServer vs;
	private VideoServerMemento memento;
	
	public PlayVideoCommand(VideoServer vs){
		this.vs = vs;
	}
	
	@Override
	public void execute() {
		memento = vs.createMemento();
		vs.play();
	}

	@Override
	public void undo() throws Exception {
		if (memento != null) vs.setMemento(memento);
	}
}
