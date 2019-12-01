package CommonClasses;

import java.io.Serializable;

public class Message implements Serializable
{
	static final long serialVersionUID = 8;
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
