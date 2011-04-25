package labs.psi.deco;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatedMessage extends MessageDecorator {
	private Date date;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); 
	
	public DatedMessage(Message message) {
		super(message);
		date = new Date();
	}
		
	public String preview(){
		return super.preview() + "\n\t Created at: " + getDateTime();
	}
	
	public void setFormat(String format) {
		dateFormat = new SimpleDateFormat(format);
	}
	
	private String getDateTime() {
	     return dateFormat.format(date);
	}
}