package labs.psi.deco;

public class Main {
	public static void main(String[] args){
		Message m = createMessage("Buy some candies.", "Tumas", true, true);
		System.out.println(m.preview());

		/* TODO:
		/* how to know if some relationship was added? */
		/* how to remove relationship */
		
		if (m instanceof EncryptedMessage)
			System.out.println("Decrypted: " + ((EncryptedMessage) m).decrypt());
	}
	
	public static Message createMessage(String message, String author, boolean dated, boolean encrypted){
		Message m = new SimpleMessage(message);
	
		if (author != null) m = new SignedMessage(m, author);
		if (dated) 			 m = new DatedMessage(m);
		if (encrypted) 		 m = new EncryptedMessage(m);
		return m;
	}
}