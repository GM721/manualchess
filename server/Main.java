


import CommonClasses.*;
import DataBase.*;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

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
            DataBase db= new DataBase();
            //long time= Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

	    Map<String, ObjectOutputStream> onlineUsers = new HashMap<String, ObjectOutputStream>();

            while (true) {
                try (
                        ServerSocket serverSocket = new ServerSocket(48651);
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
                                print_(inp);
                                int connKey = new Random().nextInt();
                                Boolean isOk;
                                String description;
                                Boolean a=db.freeNickname(inp.nickname);
                                if (!a) {
                                    isOk = false;
                                    description = "Nickname" + inp.nickname + "is already taken";
                                }
                                else
                                {
                                    if(!db.freeEmail(inp.email)) {
                                        isOk = false;
                                        description = "Email" + inp.email + "is already taken";
                                    }
                                    else{
                                        if (!db.freePassword(inp.password)){
                                            isOk = false;
                                            description = "Password" + inp.password + "is already taken";
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
                                out.flush();
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
                                }
                                else {
                                    MessageAnswer ans = new MessageAnswer(false, "message did not add",mq,time);
                                    out.writeObject(ans);
                                }
                            }
                            else if (input.operation.equals("startGettingMessages")){

                                db.setOnline(input.nickname);

                            }
                            else if (input.operation.equals("finishGettingMessages")){

                                db.setOffline(input.nickname);
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
