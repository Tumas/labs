package psi.observer;

public interface Command {
	void execute();
	void undo() throws Exception;
}
