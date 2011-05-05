package labs.psi.deco;

import java.util.UUID;

/* 
 * StringEncrypter could be found:
 * http://www.idevelopment.info/data/Programming/java/security/java_cryptography_extension/StringEncrypter.java 
 */
public class EncryptedMessage extends MessageDecorator {
	private StringEncrypter desEncrypter;
	
	public EncryptedMessage(Message message) {
		this(message, UUID.randomUUID().toString());
	}
	
	public EncryptedMessage(Message message, String passPhrase) {
		super(message);
	    desEncrypter = new StringEncrypter(passPhrase);
	    encrypt();
 	}
	
	public String getBody(){
		return decrypt();
	}
	
	public void onRemove(){
		setBody(decrypt());
	}
	
	public String decrypt(){
		return desEncrypter.decrypt(message.getBody());
	}
	
	private void encrypt() {
		message.setBody(desEncrypter.encrypt(message.getBody()));
	}
}