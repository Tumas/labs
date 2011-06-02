package psi.observer;

import java.util.Map;

public class Controller {
	private Map map;
	private CommandProcessor cmdProc = new CommandProcessor(100);
	private int commandCounter = 0;
	
	public Controller(Map map){
		this.map = map;
	}

	public void undo(){
		if (cmdProc != null)
			for (int i = 0; i < commandCounter; i--, commandCounter--)
				cmdProc.undo();
	}

	public void eventPressed(String action) {
		Command[] cmds = (Command[]) map.get(action);
		executeCommands(cmds);
	}

	private void executeCommands(Command[] cmds) {
		for (Command c : cmds){
			execute(c);
		}
		
		commandCounter = cmds.length;
	}

	private void execute(Command cmd){
		if (cmdProc != null) cmdProc.execute(cmd);
	}

}