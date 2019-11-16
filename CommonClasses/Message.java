package CommonClasses;

import java.io.Serializable;

public class Message extends Query implements Serializable
{
	public String receiver;
	public String content;
	public Message(Integer connectionKey, String nickname, String operation, String receiver, String content)
	{
		super(connectionKey, nickname, operation);
		this.receiver = receiver;
		this.content = content;
	}
}
