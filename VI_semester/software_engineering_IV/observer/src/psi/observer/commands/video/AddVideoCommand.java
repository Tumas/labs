package psi.observer.commands.video;

import psi.observer.Command;
import psi.observer.assets.Video;
import psi.observer.commands.UndoableCommandException;
import psi.observer.videoServer.VideoServer;

public class AddVideoCommand implements Command {
	private VideoServer vs;
	private Video v;
	
	public AddVideoCommand(VideoServer vs, Video v){
		this.vs = vs;
		this.v = v;
	}

	@Override
	public void execute() {
		vs.addVideoToQueue(v);
	}

	@Override
	public void undo() throws Exception {
		throw new UndoableCommandException();
	}
}
