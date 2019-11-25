package manualchess.application;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.TreeMap;

import CommonClasses.Answer;
import CommonClasses.AuthorisationAnswer;
import CommonClasses.PlayerAuthorise;
import CommonClasses.PlayerRegister;
import CommonClasses.Query;
import CommonClasses.User;

public class BigBlackBox{

    static BigBlackBox bigBlackBox;
    private boolean isOnline = false;//TODO Необходимо следить за отключениями от сети и подключениями к ней
    private boolean isEstablishing = false;
    private SQLiteDatabase cachedUsersDB;
    private ObjectOutputStream serverOutputStream;
    String ngrokHost;
    int port;
    public User currentUser = null;
    private BlackHandler handler;
    private Pair<Runnable,Runnable> SucessAndFailRunnables;
    private Context appContext;
    private boolean isAuthorising = false;
    private final static int REGISTER=20;
    private final static int REGISTERERROR=21;
    private final static int REGISTERINPROCESS=22;
    private final static int CONNECTIONERROR=-1;
    private final static int AUTHORISE=30;
    private final static int AUTHORISEERROR=31;
    private final static int AUTHORISEINPROCESS=32;
    private final static int LOGOUT=40;
    private final static int LOGOUTERROR=41;
    private final static int LOGOUTINPROCESS=42;
    private final static String CONNECTIONERRORCOMMENTARY="Connection lost";
    TreeMap<String,Pair<Runnable,Runnable>> runnableStorage=null;

    private BigBlackBox(Context appContext, String ngrokHost,int port){
        cachedUsersDB = new SQLiteCachedUsers(appContext).getWritableDatabase();
        this.ngrokHost = ngrokHost;
        this.port = port;
        establishConnection(ngrokHost,port);
        bigBlackBox = this;
        handler = new BlackHandler(Looper.getMainLooper());
        this.appContext = appContext;
        runnableStorage = new TreeMap<String,Pair<Runnable,Runnable>>();
    }

    // Плюсы приватного конструктора:
    // 1. У пользователя уйдет меньше времени на ожидание подключения
    // 2. В случае, если сюда добавится тяжеловесных операций - на них тоже уйдет меньше времени
    // 3. Не нужно гонять экземпляр класса туда-сюда. Вызвал статический метод и сразу получил единственный
    // и неповторимый экземпляр класса
    public static BigBlackBox getBigBlackBox(Context appContext, String ngrokHost,int port){
        if(bigBlackBox==null){
            bigBlackBox = new BigBlackBox(appContext,ngrokHost,port);
            return bigBlackBox;
        }else return bigBlackBox;
    }

    public static BigBlackBox getBigBlackBox(){
        if(bigBlackBox!=null){
            return bigBlackBox;
        }else throw new NullPointerException("Big black box is not created yet");
    }

    public void establishConnection(final String ngrokHost,final int port){
        //Проверка на то, не станем ли мы пытаться осуществить подключение, во время осуществления подключения или
        //во время того, как наш девайс находится онлайн
        if(!isOnline && !isEstablishing) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ObjectInputStream ois = null;
                        try {
                                isEstablishing = true;
                                Log.d("Connection", "Started establishing");
                                Socket socket = new Socket(ngrokHost, port);
                                Log.d("Socket","received");
                                ois = new ObjectInputStream(socket.getInputStream());
                                Log.d("Connection","InputStream");
                                serverOutputStream = new ObjectOutputStream(socket.getOutputStream());
                                Log.d("Connection","OutputStream");
                                serverOutputStream.flush();
                                Log.d("Connection", "Socket opened");
                                // Порядок важен: иначе существует временное окно, когда isEstablishing = false,
                                // isOnline = false, а подключение уже есть, и, чисто теоретически, другой поток тоже
                                // может начать подключение
                                isOnline = true;
                                isEstablishing = false;
                        } catch (UnknownHostException e) {
                            Log.d("Establish", "Unknown host:");
                            e.printStackTrace();
                            isEstablishing = false;
                        } catch (IOException e) {
                            Log.d("Establish", "Could not create or listen to socket");
                            e.printStackTrace();
                            isEstablishing = false;
                        }
                        while (isOnline) {
                            try {
                                Log.d("Connection", "Waiting for server");
                                Answer answer = (Answer) ois.readObject();
                                Log.d("Connection", "I've received something");
                                proceedAnswer(answer);
                                Thread.sleep(750);
                            } catch (StreamCorruptedException e) {
                                Log.d("Connection", "Server hasn't answered anything");
                                e.printStackTrace();
                                try {
                                    //Возможны два варианта:
                                    //1. Считали null
                                    //2. Пропало соединение
                                    //Интересно так же установить причину плохого соединения - подключение осуществляется на 4-ый раз
                                    //Если попытаться отправить чувака по несуществующему соединению,выскочет broken pipe
                                    Thread.sleep(750);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                                e.printStackTrace();
                                isOnline = false;
                                isEstablishing = false;
                                isAuthorising = false;
                            }
                        }
                    }
                }).start();
        }
        else{
            Log.d("Connection","Already establishing or online");
        }
    }

    private void proceedAnswer(Answer answer){
        if(answer==null){
            Log.d("Answer","NoResponseYet");
        }
        if(answer.operation.equals("registration")){
            if(answer.isOk){
                cacheUser((AuthorisationAnswer) answer);
                handler.obtainMessage(REGISTER,
                        new RunnableWithCommentary(SucessAndFailRunnables.firstObj,answer.description)).sendToTarget();
                isAuthorising=false;
            }
            else{
                handler.obtainMessage(REGISTERERROR,
                        new RunnableWithCommentary(SucessAndFailRunnables.secondObj,answer.description)).sendToTarget();
                isAuthorising=false;
                Log.d("RegAnswer",answer.description);
            }
        }
        else if(answer.operation.equals("authorisation")){
            if(answer.isOk){
                cacheUser((AuthorisationAnswer) answer);
                handler.obtainMessage(AUTHORISE,
                        new RunnableWithCommentary(SucessAndFailRunnables.firstObj,answer.description)).sendToTarget();
                isAuthorising=false;
            }
            else{
                handler.obtainMessage(AUTHORISEERROR,
                        new RunnableWithCommentary(SucessAndFailRunnables.secondObj,answer.description)).sendToTarget();
                isAuthorising=false;
                Log.d("AuthAnswer",answer.description);
            }
        }
        else if(answer.operation.equals("exit")){
            if(answer.isOk){
                unauthoriseUser(((AuthorisationAnswer)answer).nickname);
                Runnable sucess = runnableStorage.get("exit").firstObj;
                runnableStorage.put("exit", null);
                handler.obtainMessage(LOGOUT,new RunnableWithCommentary(sucess,
                        "Successfull logout")).sendToTarget();
            }else{
                Log.d("UnauthAnswer",answer.description);
                Runnable failure = runnableStorage.get("exit").secondObj;
                runnableStorage.put("exit", null);
                handler.obtainMessage(LOGOUTERROR,new RunnableWithCommentary(failure,
                        answer.description)).sendToTarget();
                failure.run();
            }
        }else{
            Log.d("WTF","Description:" + answer.description + "\nOperation:" + answer.operation);
        }
    }

    public void requestRegisterUser(final PlayerRegister playerRegister, final Runnable onSucessfullAction,
                                    final Runnable onFailedAction) {
        if (!isAuthorising) {
            establishConnection(ngrokHost,port);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isAuthorising = true;
                        int tries = 0;
                        while (!isOnline && tries < 50) {
                            Thread.sleep(100);
                            Log.d("ConnectionWait", "Waiting for connection establishment");
                            tries++;
                        }
                        if (isOnline) {
                            serverOutputStream.writeObject(playerRegister);
                            serverOutputStream.flush();
                            Log.d("requestOfRegistration", "player wroten down");
                            SucessAndFailRunnables  = new Pair<Runnable, Runnable>(onSucessfullAction,onFailedAction);
                        } else {
                            isAuthorising = false;
                            Log.d("Auth", "You are offline,connection timed out");
                            handler.obtainMessage(CONNECTIONERROR,
                                    new RunnableWithCommentary(onFailedAction,CONNECTIONERRORCOMMENTARY)).sendToTarget();
                        }
                    } catch (IOException | InterruptedException e) {
                        isAuthorising = false;
                        Log.d("IOException", "could not write to a socket");
                        handler.obtainMessage(CONNECTIONERROR,
                                new RunnableWithCommentary(onFailedAction,CONNECTIONERRORCOMMENTARY)).sendToTarget();
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            handler.obtainMessage(REGISTERINPROCESS,
                    new RunnableWithCommentary(onFailedAction,"Registration already in process")).sendToTarget();
            Log.d("Registration","Request already sent");
        }
    }


    public void requestAuthoriseUser(final PlayerAuthorise playerAuthorise,final Runnable onSucessfullAction,
                                     final Runnable onFailedAction){
        if(!isAuthorising) {
            establishConnection(ngrokHost,port);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!isAuthorised(playerAuthorise.nickname)){
                        try {
                            isAuthorising = true;
                            int tries = 0;
                            while (!isOnline && tries < 50) {
                                Thread.sleep(100);
                                Log.d("ConnectionWait", "Waiting for connection establishment");
                                tries++;
                            }
                            if (isOnline) {
                                serverOutputStream.writeObject(playerAuthorise);
                                serverOutputStream.flush();
                                SucessAndFailRunnables = new Pair<Runnable, Runnable>(onSucessfullAction, onFailedAction);
                            } else {
                                Log.d("Connection", "Authorise requested without connection");
                                handler.obtainMessage(CONNECTIONERROR,
                                        new RunnableWithCommentary(onFailedAction, CONNECTIONERRORCOMMENTARY)).sendToTarget();
                            }
                        } catch (InterruptedException | IOException ie) {
                            handler.obtainMessage(CONNECTIONERROR,
                                    new RunnableWithCommentary(onFailedAction, CONNECTIONERRORCOMMENTARY)).sendToTarget();
                            ie.printStackTrace();
                        }
                    } else{
                        switchCurrentUser(playerAuthorise.nickname);
                        onSucessfullAction.run();
                    }
                }
            }).start();
        }else{
            handler.obtainMessage(AUTHORISEINPROCESS,
                    new RunnableWithCommentary(onFailedAction,"Authorise already in process")).sendToTarget();
            Log.d("Authorisation","Request already sent");
        }
    }

    public void requestToUnauthorisation(final String nickname,final Runnable onSucessfulAction,
                                         final Runnable onFailedAction){
        establishConnection(ngrokHost,port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (runnableStorage.get("exit") == null) {
                    runnableStorage.put("exit", new Pair<Runnable, Runnable>(null, null));
                    try {
                        int tries = 0;
                        while (!isOnline && tries < 50) {
                            Thread.sleep(100);
                            tries++;
                        }
                        if (isOnline) {
                            Log.d("DeauthorisationRequest", "CursorRequested");
                            Cursor cursor = cachedUsersDB.query(LoggedUsersContract.LoggedUsers.tableName,
                                    new String[]{LoggedUsersContract.LoggedUsers.connectionKey},
                                    LoggedUsersContract.LoggedUsers.nickname + "=" + "'" + nickname + "'", null,
                                    null, null, null);
                            Log.d("DeauthorisationRequest", "CursorReceived");
                            cursor.moveToNext();
                            int connectionKey = cursor.getInt(0);
                            cursor.close();
                            Log.d("DeathorisationRequest", "Connection key received");
                            Query query = new Query(connectionKey, nickname, "exit");
                            Log.d("DeauthorisationRequest", "Query generated");
                            serverOutputStream.writeObject(query);
                            Log.d("DeauthorisationRequest", "Succesfull");
                            runnableStorage.put("exit", new Pair<Runnable, Runnable>(onSucessfulAction, onFailedAction));

                        } else {
                            runnableStorage.put("exit", null);
                            Log.d("Connection", "Unauthorisation could not be done:connection lost");
                            handler.obtainMessage(CONNECTIONERROR,
                                    new RunnableWithCommentary(onFailedAction, CONNECTIONERRORCOMMENTARY)).sendToTarget();
                        }
                    } catch (IOException | InterruptedException e) {
                        runnableStorage.put("exit", null);
                        e.printStackTrace();
                        handler.obtainMessage(CONNECTIONERROR,
                                new RunnableWithCommentary(onFailedAction, CONNECTIONERRORCOMMENTARY)).sendToTarget();
                    }

                }else {
                    handler.obtainMessage(LOGOUTINPROCESS,
                            new RunnableWithCommentary(onFailedAction,"Log out already in process")).sendToTarget();
                    Log.d("LOGOUT", "INPROCESS");
                }
            }
        }).start();
    }

    private void cacheUser(final AuthorisationAnswer authAnswer){
        Log.d("Answer",authAnswer.nickname);
        cachedUsersDB.execSQL("INSERT INTO " + LoggedUsersContract.LoggedUsers.tableName +
                " VALUES (\'" +  authAnswer.nickname +"\', \'" + authAnswer.connectionKey + "\');");
        currentUser = new User(authAnswer.nickname,authAnswer.connectionKey);
    }

    private void unauthoriseUser(String nickname){
        cachedUsersDB.execSQL("DELETE FROM " + LoggedUsersContract.LoggedUsers.tableName + " WHERE "+
                LoggedUsersContract.LoggedUsers.nickname+"="+"'"+nickname+"'");
        Log.d("UnauthAnswer","Unauthorisation completed");
        currentUser=null;
    }

    private boolean isAuthorised(String nickname){
        Cursor cursor = cachedUsersDB.query(LoggedUsersContract.LoggedUsers.tableName,
                new String[]{LoggedUsersContract.LoggedUsers.nickname,LoggedUsersContract.LoggedUsers.connectionKey},
                LoggedUsersContract.LoggedUsers.nickname + "=" + "'" + nickname + "'",null,null,
                null,null);
        if(cursor.getCount()==0) return false;
        else {
            return true;
        }
    }

    public void switchCurrentUser(final String nickname){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = cachedUsersDB.query(LoggedUsersContract.LoggedUsers.tableName,
                        new String[]{LoggedUsersContract.LoggedUsers.nickname,LoggedUsersContract.LoggedUsers.connectionKey},
                        LoggedUsersContract.LoggedUsers.nickname + "=" + "'" + nickname + "'",null,null,
                        null,null);
                cursor.moveToNext();
                currentUser = new User(cursor.getString(0),cursor.getInt(1));
            }
        }).start();
    }

    public String[] getCachedUsers(){
        ArrayList<String> cachedUsers = new ArrayList<String>();
        Cursor cursor = cachedUsersDB.query(
                LoggedUsersContract.LoggedUsers.tableName,
                new String[] {LoggedUsersContract.LoggedUsers.nickname, LoggedUsersContract.LoggedUsers.connectionKey},
                null,null,null,null,null);
        while(cursor.moveToNext()){
            cachedUsers.add(cursor.getString(0));
        }
        return cachedUsers.toArray(new String[cachedUsers.size()]);
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
                cursor.close();
            }
            }).start();
        }

        private class BlackHandler extends Handler{
        BlackHandler(Looper looper){
            super(looper);
        }
            @Override
            public void handleMessage(Message msg) {
                ((RunnableWithCommentary)msg.obj).runnable.run();
                Toast.makeText(appContext,((RunnableWithCommentary)msg.obj).commentary,Toast.LENGTH_LONG).show();

            }
        }

        private class RunnableWithCommentary{
        Runnable runnable;
        String commentary;
        RunnableWithCommentary(Runnable runnable,String commentary){
            this.runnable = runnable;
            this.commentary = commentary;
        }
        }

        private class Pair<T_1,T_2> {
            T_1 firstObj;
            T_2 secondObj;
            Pair(T_1 firstObj,T_2 secondObj){
                this.firstObj = firstObj;
                this.secondObj = secondObj;
            }
        }

        private class Description{
            String nickname;
            String operation;
            Description(String nickname,String operation){
                this.nickname = nickname;
                this.operation = operation;
            }
        }
}