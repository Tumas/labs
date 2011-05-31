package psi.observer.commands.video;

import psi.observer.Command;
import psi.observer.videoServer.VideoServer;
import psi.observer.videoServer.VideoServerMemento;

public class StopVideoCommand implements Command {
	private VideoServer vs;
	private VideoServerMemento memento;
	
	public StopVideoCommand(VideoServer vs){
		this.vs = vs;
	}
	
	@Override
	public void execute() {
		memento = vs.createMemento();
		vs.stop();
	}

	@Override
	public void undo() throws Exception {
		if (memento != null) vs.setMemento(memento);
	}
}
