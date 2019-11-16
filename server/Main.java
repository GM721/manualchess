import CommonClasses.*;
import DataBase.DataBase;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.Random;

public class Main {
	static void print_(PlayerRegister player)
	{
		System.out.print("1:");
		System.out.println(player.nickname);
		System.out.println(player.operation);
		System.out.println(player.password);
		System.out.println(player.email);
	}
	public static void main(String[] args) {
		try
		{
			DataBase db = new DataBase();
			while (true) {
				try (
						ServerSocket serverSocket = new ServerSocket(48657);
						Socket clientSocket = serverSocket.accept();
						ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
						ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
						ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				    ) {
					System.out.println("AAAAA");
					outputStream.flush();
					Query input;
					System.out.println("00");
					try {
						while ((input = (Query)in.readObject()) != null) {
							System.out.print(input.operation);
							System.out.println("ll");
							if (input.operation.equals("registration"))
							{
								System.out.println("[[");
								PlayerRegister inp = (PlayerRegister)input;
								print_(inp);
								int connKey = new Random().nextInt();
								Boolean isOk;
								String description;
								if (!db.isFree(inp.nickname))
								{
									isOk = false;
									description = "Nickname" + inp.nickname + "is already taken";
								}
								else
								{
									isOk = true;
									description = "Registration is finished correctly";
									db.addUser(inp, connKey);
								}
								System.out.println(connKey);
								Answer ans = new Answer(isOk, inp.nickname, connKey, inp.operation, description);
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
								Answer ans = new Answer(isOk, inp.nickname, inp.connectionKey, inp.operation, description);
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
								}
								else
								{
									isOk = false;
									description = "Exit is aborted";
								}
								System.out.println(isOk);
								Answer ans = new Answer(isOk, input.nickname, input.connectionKey, input.operation, description);
								out.writeObject(ans);
							}
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
						System.out.println("EOF");
					}
				} catch (IOException e) {
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
