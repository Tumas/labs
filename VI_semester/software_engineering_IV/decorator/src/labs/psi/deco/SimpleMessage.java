package labs.psi.deco;

public class SimpleMessage implements Message {

	private String text;
	
	public SimpleMessage(String text){
		this.text = text;
	}
	
	@Override
	public String preview() {
		return getBody();
	}

	public String getBody() {
		return text;
	}

	public void setBody(String text) {
		this.text = text;
	}
}
