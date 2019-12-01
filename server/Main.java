import CommonClasses.*;
import DataBase.*;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Main{
	public static DataBase db;
	public static Map<String, ObjectOutputStream> onlineUsers;
	public static MessageSender messageSender;
	public static void main(String[] args) {
		try
		{
			db = new DataBase();
			//long time= Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			onlineUsers = new HashMap<String, ObjectOutputStream>();
			messageSender = new MessageSender(onlineUsers);

			ServerSocket serverSocket = new ServerSocket(48651);
			while (true) {
				try {
					Socket clientSocket = serverSocket.accept();
					//ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
					//ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
					//ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

					System.out.println("New client");
					//outputStream.flush();
					UserThread thread = new UserThread(db, clientSocket, onlineUsers, messageSender);
					thread.start();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


class UserThread extends Thread
{
	DataBase db;
	Socket clientSocket;
	Map<String, ObjectOutputStream> onlineUsers;
	MessageSender messageSender;
	public UserThread(DataBase db, Socket clientSocket, Map<String, ObjectOutputStream> onlineUsers, MessageSender messageSender)
	{
		this.clientSocket = clientSocket;
		this.messageSender = messageSender;
		this.db = db;
		this.onlineUsers = onlineUsers;
	}
	public void run()
	{
		Query input = new Query();
		try {
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
			while ((input = (Query) in.readObject()) != null) {
				if (!onlineUsers.containsKey(input.nickname))
				{
					onlineUsers.put(input.nickname, out);
				}
				System.out.println(input.operation);
				System.out.println(input.nickname);
				System.out.println(input.connectionKey);
				if (input.operation.equals("registration"))
				{
					PlayerRegister inp = (PlayerRegister)input;
					int connKey = new Random().nextInt();
					Boolean isOk;
					String description;
					if (!db.freeNickname(inp.nickname))
					{
						isOk = false;
						description = "Nickname " + inp.nickname + " is already taken";
					}
					else
					{
						if(!db.freeEmail(inp.email)) {
							isOk = false;
							description = "Email " + inp.email + " is already taken";
						}
						else{
							if (!db.freePassword(inp.password)){
								isOk = false;
								description = "Password " + inp.password + " is already taken";
							}
							else {
								isOk = true;
								description = "Registration is finished correctly";
								db.addUser(inp, connKey);
							}
						}
					}
					AuthorisationAnswer ans = new AuthorisationAnswer(isOk, inp.nickname, connKey, inp.operation, description);
					out.writeObject(ans);
				}
				else if (input.operation.equals("authorisation"))
				{
					PlayerAuthorise inp = (PlayerAuthorise)input;
					Boolean isOk;
					String description;
					int connKey = new Random().nextInt();
					if (db.checkUser(inp))
					{
						isOk = true;
						description = "Authorisation is finished correctly";
						db.setConnKey(inp.nickname, connKey);
						inp.connectionKey = connKey;
					}
					else
					{
						isOk = false;
						description = "Authorisation is aborted";
					}
					AuthorisationAnswer ans = new AuthorisationAnswer(isOk, inp.nickname, inp.connectionKey, inp.operation, description);
					out.writeObject(ans);
				}
				else if (input.operation.equals("exit"))
				{
					Boolean isOk;
					String description;
					if (input.connectionKey == db.getConnectionKey(input.nickname))
					{
						isOk = true;
						description = "Exit is finished correctly";
						db.dropConnKey(input.nickname);
						db.setOffline(input.nickname);
						onlineUsers.remove(input.nickname);
					}
					else
					{
						isOk = false;
						description = "Exit is aborted";
					}
					AuthorisationAnswer ans = new AuthorisationAnswer(isOk, input.nickname, input.connectionKey, input.operation, description);
					out.writeObject(ans);
				}
				else if (input.operation.equals("message")){
					MessageQuery mq = (MessageQuery)input;
					java.util.Date time=db.addMessage(mq);
					System.out.println("======"  + time);
					if(!time.equals(null)){
					MessageAnswer ans = new MessageAnswer(true, "message added",mq,CommonClasses.Calendar.fromDate(new Date()));
						out.writeObject(ans);
						messageSender.send(mq);
					}
					else {
						MessageAnswer ans = new MessageAnswer(false, "message did not add",mq,null);
						out.writeObject(ans);
					}
				}
				else if (input.operation.equals("startGettingMessages"))
				{
					RequestMessagesQuery query = (RequestMessagesQuery)input;
					MessagesAnswer ans = db.getNewMessages(query);
					out.writeObject(ans);
				}
				else if (input.operation.equals("stopGettingMessages"))
				{
					onlineUsers.remove(input.nickname);
				}
				else if (input.operation.equals("collocation"))
				{
					CollocutorQuery query = (CollocutorQuery)input;
					Boolean isOk;
					String description;
					if (db.isUser(query.collocutor))
					{
						isOk = true;
						description = "Everything is ok";
					}
					else
					{
						isOk = false;
						description = "No such user";
					}
					CollocutorAnswer ans = new CollocutorAnswer(isOk, description, query.collocutor);
					out.writeObject(ans);
				}
				out.flush();
				System.out.println("-------------------------------------------------");
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			//Answer ans = new Answer();
			//ans.isOk = false;
			//out.writeObject(ans);
			e.printStackTrace();
		}
		catch (EOFException | StreamCorruptedException e)
		{
			onlineUsers.remove(input.nickname);
			try
			{
				clientSocket.close();
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
