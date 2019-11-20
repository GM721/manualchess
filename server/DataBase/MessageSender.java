package DataBase;

import CommonClasses.*;
import java.util.*;
import java.io.*;

public class MessageSender
{
	static Map<String, ObjectOutputStream> onlineUsers;
	public MessageSender(Map<String, ObjectOutputStream> onlineUsers)
	{
		this.onlineUsers = onlineUsers;
	}
	static public void send(MessageQuery message) throws IOException
	{
		if (onlineUsers.containsKey(message.collocutor))
		{
			MessageAnswer ans = new MessageAnswer(true, "new message", message, new Date());
			onlineUsers.get(message.collocutor).writeObject(ans);
		}
	}
}
