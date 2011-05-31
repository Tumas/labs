package psi.observer;

import java.util.ArrayDeque;

public class CommandProcessor {
	ArrayDeque<Command> commandStack = new ArrayDeque<Command>();

	private int stackDepth;
	private int currentDepth = 0;
	
	public CommandProcessor(int stackDepth){
		this.stackDepth = stackDepth;
	}
	
	public void execute(Command cmd) {
		commandStack.push(cmd);
		cmd.execute();
		
		if (currentDepth == stackDepth)
			commandStack.removeLast();
		else
			currentDepth++;
	}
	
	public void undo(){
		if (currentDepth > 0){
			try {
				Command cmd = commandStack.pop();
				cmd.undo();
				currentDepth--;
			} catch (Exception e) {
				resetStack();
			}
		}
	}
	
	private void resetStack(){
		commandStack = new ArrayDeque<Command>();
		currentDepth = 0;
	}
}