package psi.observer;

public class Controller {
	CommandProcessor cmdProc = new CommandProcessor(5);
	
	public void execute(Command cmd){
		cmdProc.execute(cmd);
	}
	
	public void undo(){
		cmdProc.undo();
	}
}