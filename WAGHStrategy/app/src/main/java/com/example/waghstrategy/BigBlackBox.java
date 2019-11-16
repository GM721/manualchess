package com.example.waghstrategy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class BigBlackBox{

    boolean isOnline = false;
    boolean isEstablishing;//Проблема с созданием множественных подключений - создастся множество сокетов - рофлан поминки
    SQLiteDatabase cachedUsersDB;
    ObjectOutputStream serverOutputStream;
    ConnectivityManager connectivityManager;
    String ngrokHost;
    int port;


    BigBlackBox(Context appContext, String ngrokHost,int port){
        cachedUsersDB = new SQLiteCachedUsers(appContext).getWritableDatabase();
        connectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.ngrokHost = ngrokHost;
        this.port = port;
        establishConnection(ngrokHost,port);
    }

    public void establishConnection(final String ngrokHost,final int port){
        try{
            isEstablishing = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ObjectInputStream ois = null;
                    try {
                        if(connectivityManager.getActiveNetworkInfo() != null &&
                                connectivityManager.getActiveNetworkInfo().isConnected()) {
                            Log.d("Connection", "Started establishing");
                            Socket socket = new Socket(ngrokHost, port);
                            ois = new ObjectInputStream(socket.getInputStream());
                            serverOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            serverOutputStream.flush();
                            Log.d("Connection", "Socket opened");
                            isOnline = true;
                        }
                    }catch (UnknownHostException e) {
                        Log.d("Establish","Unknown host:");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.d("Establish","Could not create or listen to socket");
                        e.printStackTrace();
                    }
                    while(isOnline){
                        try {
                            Log.d("Connection", "Waiting for server");
                            Answer answer = (Answer) ois.readObject();
                            Log.d("Connection", "I've received something");
                            proceedAnswer(answer);
                            Thread.sleep(750);
                        }catch (IOException e){
                            Log.d("Connection","Server hasn't answered anything");
                            try {
                                Thread.sleep(750);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }catch (InterruptedException | ClassNotFoundException e){
                            e.printStackTrace();
                            isOnline = false;
                        }
                    }
                    }
                }).start();
        }catch (Exception e){
            isOnline = false;
            e.printStackTrace();
        }
    }


    private void proceedAnswer(Answer answer){
        if(answer==null){
            Log.d("Answer","NoResponseYet");
        }
        if(answer.operation.equals("registration")){
            if(answer.isOk){
                cacheUser(answer);
            }
            else{
                Log.d("RegAnswer",answer.description);
            }
        }
        if(answer.operation.equals("authorisation")){
            if(answer.isOk){
                cacheUser(answer);
            }
            else{
                Log.d("AuthAnswer",answer.description);
            }
        }if(answer.operation.equals("exit")){
            if(answer.isOk){
                unauthoriseUser(answer.name);
            }else{
                Log.d("UnauthAnswer",answer.description);
            }
        }
    }

    public void requestRegisterUser(final PlayerRegister player) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int tries=0;
                        while(!isOnline && tries<100){
                            Thread.sleep(100);
                            Log.d("ConnectionWait","Waiting for connection establishment");
                            tries++;
                        }
                        if(isOnline) {
                            serverOutputStream.writeObject(player);
                            serverOutputStream.flush();
                            Log.d("requestOfRegistration","player wroten down");
                        }else {
                            Log.d("Auth","You are offline,connection timed out");
                        }
                    }catch(IOException e){
                        Log.d("IOException","could not write to a socket");
                        e.printStackTrace();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    public void requestAuthoriseUser(final PlayerAuthorise playerAuthorise){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int tries = 0;
                    while (!isOnline && tries < 100) {
                        Thread.sleep(100);
                        Log.d("ConnectionWait", "Waiting for connection establishment");
                        tries++;
                    if(isOnline){
                        serverOutputStream.writeObject(playerAuthorise);
                        serverOutputStream.flush();
                    }
                    else{
                        Log.d("Connection","Authorise waiting for connection establishment");
                    }
                    }
                }catch (InterruptedException | IOException ie){
                }
            }
        }).start();
    }

    public void requestToUnauthorisation(final String nickname){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int tries=0;
                    while(!isOnline && tries<100){
                        Thread.sleep(100);
                        tries++;
                    }
                    if(isOnline) {
                        Log.d("DeauthorisationRequest","CursorRequested");
                        Cursor cursor = cachedUsersDB.query(LoggedUsersContract.LoggedUsers.tableName,
                                new String[]{LoggedUsersContract.LoggedUsers.connectionKey},
                                LoggedUsersContract.LoggedUsers.nickname + "=" +"'" + nickname +"'",null,
                                null,null,null);
                        Log.d("DeauthorisationRequest","CursorReceived");
                        cursor.moveToNext();
                        int connectionKey = cursor.getInt(0);
                        Log.d("DeathorisationRequest","Connection key received");
                        Query query = new Query(connectionKey, nickname, "exit");
                        Log.d("DeauthorisationRequest","Query generated");
                        serverOutputStream.writeObject(query);
                        Log.d("DeauthorisationRequest","Succesfull");
                    }else{
                        Log.d("Connection","Unauthorisation could not be done:connection lost");
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void cacheUser(final Answer answer){
        Log.d("Answer",answer.name);
        cachedUsersDB.execSQL("INSERT INTO " + LoggedUsersContract.LoggedUsers.tableName +
                " VALUES (\'" +  answer.name +"\', \'" + answer.connectionKey + "\');");
    }

    private void unauthoriseUser(String nickname){
        cachedUsersDB.execSQL("DELETE FROM " + LoggedUsersContract.LoggedUsers.tableName + " WHERE "+
                LoggedUsersContract.LoggedUsers.nickname+"="+"'"+nickname+"'");
        Log.d("UnauthAnswer","Unauthorisation completed");
    }

    public void logDatabase(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = cachedUsersDB.query(
                        LoggedUsersContract.LoggedUsers.tableName,
                        new String[] {LoggedUsersContract.LoggedUsers.nickname, LoggedUsersContract.LoggedUsers.connectionKey},
                        null,null,null,null,null);
                while(cursor.moveToNext()){
                    Log.d("SomeInfo:",cursor.getString(0));
                }
            }
            }).start();
        }
}
//TODO HTTP connection class? Ip of server?