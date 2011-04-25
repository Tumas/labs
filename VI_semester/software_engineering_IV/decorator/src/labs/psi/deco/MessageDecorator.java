package labs.psi.deco;

public abstract class MessageDecorator implements Message {
	protected Message message;
	
	public MessageDecorator(Message decoratedMessage) {
		this.message = decoratedMessage;
	}
	
	public String preview(){
		return message.preview();
	}
	
	public void setBody(String body){
		message.setBody(body);
	}
	
	public String getBody(){
		return message.getBody();
	}
}