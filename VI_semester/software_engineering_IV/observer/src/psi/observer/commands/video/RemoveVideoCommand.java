package psi.observer.commands.video;

import psi.observer.Command;
import psi.observer.assets.Video;
import psi.observer.commands.UndoableCommandException;
import psi.observer.videoServer.VideoServer;

public class RemoveVideoCommand implements Command {
	private VideoServer vs;
	private Video v;
	
	public RemoveVideoCommand(VideoServer vs, Video v){
		this.vs = vs;
		this.v = v;
	}

	@Override
	public void execute() {
		vs.removeFromQueue(v);
	}

	@Override
	public void undo() throws Exception {
		throw new UndoableCommandException();
	}
}