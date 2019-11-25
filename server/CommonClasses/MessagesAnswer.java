package CommonClasses;

import java.io.Serializable;
import java.util.ArrayList;

public class MessagesAnswer extends Answer implements Serializable
{
	private static final long serialVersionUID = 6;
	public ArrayList<Message> messages;
	public MessagesAnswer()
	{
		super(true, "startGettingMessages", "Everything is ok");
		messages = new ArrayList<Message>();
	}
}
