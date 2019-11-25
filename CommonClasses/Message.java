package CommonClasses;

import java.io.Serializable;
import java.util.Calendar;

public class Message implements Serializable
{
	public String sender;
	public String receiver;
	public String message;
	public Calendar date;
	public Message(String sender, String receiver, String message, Calendar date)
	{
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.date = date;
	}
}
