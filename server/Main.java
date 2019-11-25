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
	static void print_(PlayerRegister player)
	{
		System.out.print("1:");
		System.out.println(player.nickname);
		System.out.println(player.operation);
		System.out.println(player.password);
		System.out.println(player.email);
	}
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
			System.out.println(serverSocket.getSoTimeout());
			while (true) {
				try {
						Socket clientSocket = serverSocket.accept();
						//ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						//ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
						//ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				    
					System.out.println("AAAAA");
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
		Query input;
                    System.out.println("00");
                    try {
			 
			    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                        while ((input = (Query) in.readObject()) != null) {
				if (!onlineUsers.containsKey(input.nickname))
				{
					onlineUsers.put(input.nickname, out);
				}
                            System.out.print(input.operation);
                            System.out.println("ll");
                            if (input.operation.equals("registration"))
                            {
                                System.out.println("[[");
                                PlayerRegister inp = (PlayerRegister)input;
                                //print_(inp);
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
                                System.out.println(connKey);
                                AuthorisationAnswer ans = new AuthorisationAnswer(isOk, inp.nickname, connKey, inp.operation, description);
                                out.writeObject(ans);
                            }
                            else if (input.operation.equals("authorisation"))
                            {
                                PlayerAuthorise inp = (PlayerAuthorise)input;
                                System.out.println(inp.nickname);
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
                                System.out.println("HHHH");
                                Boolean isOk;
                                String description;
                                System.out.println(input.nickname);
                                System.out.println(input.connectionKey);
                                System.out.println(db.getConnectionKey(input.nickname));
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
                                System.out.println(isOk);
                                AuthorisationAnswer ans = new AuthorisationAnswer(isOk, input.nickname, input.connectionKey, input.operation, description);
                                out.writeObject(ans);
                            }
                            else if (input.operation.equals("message")){
                                MessageQuery mq = (MessageQuery)input;
                                Date time=db.addMessage(mq);
                                System.out.println(time);
                                if(!time.equals(null)){
                                    MessageAnswer ans = new MessageAnswer(true, "message added",mq,time);
                                    out.writeObject(ans);
				    messageSender.send(mq);
                                }
                                else {
                                    MessageAnswer ans = new MessageAnswer(false, "message did not add",mq,time);
                                    out.writeObject(ans);
                                }
                            }
                            else if (input.operation.equals("startGettingMessages"))
			    {
				    RequestMessagesQuery query = (RequestMessagesQuery)input;
				    MessagesAnswer ans = db.getNewMessages(query);
				    out.writeObject(ans);
			    }
			    out.flush();
                    }

                    } catch (ClassNotFoundException e) {
                        System.out.print("2:");
                        e.printStackTrace();
                    }
                    catch (SQLException e)
                    {
                        System.out.println("SQLExceprion");
                        e.printStackTrace();
                    }
                    catch (EOFException e)
                    {
			    //clientSocket.close();
                        System.out.println("EOF");
                    }
		    catch (Exception e)
		    {
			    e.printStackTrace();
		    }
               }
	
}
