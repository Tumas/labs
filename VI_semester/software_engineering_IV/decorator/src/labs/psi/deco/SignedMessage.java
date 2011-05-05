package labs.psi.deco;

public class SignedMessage extends MessageDecorator {
	private String name;
	
	public SignedMessage(Message message) {
		super(message);
	}

	public SignedMessage(Message message, String author) {
		this(message);
		sign(author);
	}

	public void sign(String authorName){
		this.name = authorName;
	}
	
	public String preview(){
		return super.preview() + "\n\t Author: " + this.name; 
	}
}