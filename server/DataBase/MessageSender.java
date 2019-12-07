package DataBase;

import CommonClasses.*;
import java.util.*;
import java.io.*;

public class MessageSender
{
	static Map<String, Pair<ObjectOutputStream, ObjectInputStream>> onlineUsers;
	public MessageSender(Map<String, Pair<ObjectOutputStream, ObjectInputStream>> onlineUsers)
	{
		this.onlineUsers = onlineUsers;
	}
	static public void send(MessageQuery message) throws IOException
	{
		if (onlineUsers.containsKey(message.collocutor))
		{
			System.out.println("YES");
			CommonClasses.Calendar cal = CommonClasses.Calendar.fromDate(new Date());
			System.out.println(cal);
			MessageAnswer ans = new MessageAnswer(true, "new message", message, /*CommonClasses.Calendar.fromDate(new Date())*/cal);
			onlineUsers.get(message.collocutor).getValue0().writeObject(ans);
		}
	}
}
