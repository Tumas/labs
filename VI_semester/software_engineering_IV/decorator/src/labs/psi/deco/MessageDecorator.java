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
	
	public static MessageDecorator getRole(MessageDecorator m, String className){
		if (m.getClass().getSimpleName().equals(className)) 
			return m;
		
		if (!(m.message instanceof MessageDecorator)) 
			return null;
		
		return MessageDecorator.getRole((MessageDecorator) m.message, className);
	}
	
	public static Message removeRole(MessageDecorator m, String className){
		if (m.getClass().getSimpleName().equals(className)) {
			if (className == "EncryptedMessage") m.setBody(((EncryptedMessage)m).decrypt());
			
			if (!(m.message instanceof MessageDecorator)) 
				return m.message;
			else 
				return MessageDecorator.removeRole((MessageDecorator) m.message, className);
		}
		
		if (!(m.message instanceof MessageDecorator)) 
			return m;

		m.message = MessageDecorator.removeRole((MessageDecorator) m.message, className);
		return m;
	}
}