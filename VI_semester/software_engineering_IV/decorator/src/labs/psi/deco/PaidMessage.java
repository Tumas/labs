package labs.psi.deco;

public class PaidMessage extends MessageDecorator {
	private double price;
	private double costPerSymbol = 0.05;
	
	public PaidMessage(Message message) {
		super(message);
	}

	public double getPrice(){
		return message.getBody().length() * costPerSymbol;
		//double price = message.getBody().length() * costPerSymbol;
		//return String.format("%5.2f", price);
	}
	
	public String preview(){
		return super.preview() + "\n Price: " + getPrice();
	}
}