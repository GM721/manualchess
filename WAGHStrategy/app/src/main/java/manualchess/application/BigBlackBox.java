package manualchess.application;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
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
import CommonClasses.Calendar;
import java.util.TreeMap;

import CommonClasses.Answer;
import CommonClasses.AuthorisationAnswer;
import CommonClasses.CollocutorAnswer;
import CommonClasses.CollocutorQuery;
import CommonClasses.DiceAnswer;
import CommonClasses.MessageAnswer;
import CommonClasses.MessageQuery;
import CommonClasses.MessagesAnswer;
import CommonClasses.PlayerAuthorise;
import CommonClasses.PlayerRegister;
import CommonClasses.Query;
import CommonClasses.RequestMessagesQuery;
import CommonClasses.StartBattleQuery;
import CommonClasses.StartBattleRequest;
import CommonClasses.User;
import UtilClasses.MutatorOfNotifiableArrayList;
import UtilClasses.MutatorOfNotifiableTreeMap;
import UtilClasses.NotifiableArrayList;
import UtilClasses.NotifiableTreeMap;
import UtilClasses.Pair;
import UtilClasses.RunnableWithResource;

public class BigBlackBox{
//TODO когда выходишь на главный экран,почему-то продолжаешь принимать сообщения,иначе говоря,на stopGettingMessages надо
//TODO прекратить получать сообщения
    private boolean isOnline = false;
    private boolean isEstablishing = false;
    private boolean isAuthorising = false;
    private boolean isPrepared = false;
    static BigBlackBox bigBlackBox;
    private SQLiteDatabase cachedUsersDB;
    private ObjectOutputStream serverOutputStream;
    String ngrokHost;
    int port;
    public User currentUser = null;
    public String currentCollocutor;
    private AuthorisationHandler authHandler;
    private DialogueHandler dialogueHandler;
    private Pair<Runnable,Runnable> SucessAndFailRunnables;
    private Pair<RunnableWithResource<Integer>,RunnableWithResource<String>> diceResultRunnables;
    private RunnableWithResource<String> diceRequestRunnable;
    private Context appContext;
    private TreeMap<String,CommonClasses.Message> lastCachedMessages;
    private ArrayList<CommonClasses.Message> cachedCollocation;
    private NotifiableTreeMap<String,CommonClasses.Message> notifiableLastMessages;
    private NotifiableArrayList<CommonClasses.Message> notifiableCollocation;
    private MutatorOfNotifiableTreeMap<String,CommonClasses.Message> mutatorOfNotifiableTreeMap;
    private MutatorOfNotifiableArrayList<CommonClasses.Message> mutatorOfNotifiableCollocation;
    private TreeMap<String,Pair<Runnable,Runnable>> runnableStorage;


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

    private BigBlackBox(Context appContext, String ngrokHost,int port){
        cachedUsersDB = new SQLiteCachedUsers(appContext).getWritableDatabase();
        this.ngrokHost = ngrokHost;
        this.port = port;
        establishConnection(ngrokHost,port);
        bigBlackBox = this;
        authHandler = new AuthorisationHandler(Looper.getMainLooper());
        dialogueHandler = new DialogueHandler(Looper.getMainLooper());
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
                                for (String s:runnableStorage.keySet()) {
                                    runnableStorage.put(s,null);
                                }
                                diceResultRunnables =null;
                            }
                        }
                    }
                }).start();
        }
        else{
            Log.d("Connection","Already establishing or online");
        }
    }
//При деавторизации и последующей авторизации происходит дублирование диалогов
//При нажатии кнопки назад и при log out не происходит отправки запросов на остановку получения сообщений
    private void proceedAnswer(Answer answer){
        if(answer==null){
            Log.d("Answer","NoResponseYet");
        }
        if(answer.operation.equals("registration")){
            if(answer.isOk){
                cacheUser((AuthorisationAnswer) answer);
                authHandler.obtainMessage(REGISTER,
                        new RunnableWithCommentary(SucessAndFailRunnables.firstObj,answer.description)).sendToTarget();
                isAuthorising=false;
            }
            else{
                authHandler.obtainMessage(REGISTERERROR,
                        new RunnableWithCommentary(SucessAndFailRunnables.secondObj,answer.description)).sendToTarget();
                isAuthorising=false;
                Log.d("RegAnswer",answer.description);
            }
        }
        else if(answer.operation.equals("authorisation")){
            if(answer.isOk){
                cacheUser((AuthorisationAnswer) answer);
                authHandler.obtainMessage(AUTHORISE,
                        new RunnableWithCommentary(SucessAndFailRunnables.firstObj,answer.description)).sendToTarget();
                isAuthorising=false;
            }
            else{
                authHandler.obtainMessage(AUTHORISEERROR,
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
                authHandler.obtainMessage(LOGOUT,new RunnableWithCommentary(sucess,
                        "Successfull logout")).sendToTarget();
            }else{
                Log.d("UnauthAnswer",answer.description);
                Runnable failure = runnableStorage.get("exit").secondObj;
                runnableStorage.put("exit", null);
                authHandler.obtainMessage(LOGOUTERROR,new RunnableWithCommentary(failure,
                        answer.description)).sendToTarget();
                failure.run();
            }
        }else if(answer.operation.equals("startGettingMessages")){
            if(answer.isOk) {
                MessagesAnswer messagesAnswer = (MessagesAnswer) answer;
                ArrayList<CommonClasses.Message> messages = messagesAnswer.messages;
                for (CommonClasses.Message message:messages) {
                    addMessageToDB(message);
                }
            }else{
                Log.d("Message array",answer.description);
            }
        }else if(answer.operation.equals("message")){
            if(answer.isOk){
                MessageAnswer messageAnswer = (MessageAnswer) answer;
                addMessageToDB(new CommonClasses.Message(messageAnswer));
                Log.d("Message_ok","Received");
            }
            else{
                Log.d("Message",answer.description);
            }
        }else if(answer.operation.equals("collocation")){
            if(answer.isOk){
                Log.d("Collocation","isOk");
                Message message = dialogueHandler.obtainMessage(0,runnableStorage.get("Collocutor search").firstObj);
                currentCollocutor = ((CollocutorAnswer) answer).collocutor;
                message.sendToTarget();
                runnableStorage.put("Collocutor search",null);
            }else{
                Message message = dialogueHandler.obtainMessage(-5,
                        new Pair<Runnable,String> (runnableStorage.get("Collocutor search").secondObj,answer.description));
                message.sendToTarget();
                runnableStorage.put("Collocutor search",null);
            }
        }else if(answer.operation.equals("startBattle")){
            Log.d("Start battle","received");
            if(answer.isOk){
                StartBattleRequest startBattleRequest = (StartBattleRequest) answer;
                Log.d("Start battle","is ok");
                if(diceResultRunnables==null) {
                    Log.d("Dice result","is null");
                    dialogueHandler.obtainMessage(21,
                            new Pair<RunnableWithResource<String>, String>(diceRequestRunnable,
                                    startBattleRequest.collocutor)).sendToTarget();
                }else{
                    try {
                        serverOutputStream.writeObject(new StartBattleQuery(currentUser.connectionKey,
                                currentUser.nickname, startBattleRequest.collocutor,
                                false,"battleResponse"));
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }else{
                Log.d("Start battle","request received, but something went wrong on the server side");
            }
        }else if(answer.operation.equals("diceRoll")){
            Log.d("Dice roll","received");
            DiceAnswer diceAnswer = (DiceAnswer) answer;
            if(answer.isOk){
                Log.d("Dice roll","ok");
                dialogueHandler.obtainMessage(20,new Pair<RunnableWithResource<Integer>,Pair<Integer,Integer>>(
                        diceResultRunnables.firstObj,new Pair<Integer, Integer>(diceAnswer.userResult,diceAnswer.collocutorResult)
                )).sendToTarget();
                diceResultRunnables = null;
            }else{
                Log.d("Dice roll",diceAnswer.description);
                dialogueHandler.obtainMessage(-10,new Pair<RunnableWithResource<String>,String>(
                        diceResultRunnables.secondObj,diceAnswer.description)).sendToTarget();
                diceResultRunnables = null;
            }
        }
        else{
            Log.d("WTF","Description:" + answer.description + "\nOperation:" + answer.operation);
        }
    }

    public void requestRegisterUser(final PlayerRegister playerRegister, final Runnable onSucessfullAction,
                                    final Runnable onFailedAction) {
        if (!isAuthorising) {
            if(!isOnline) establishConnection(ngrokHost,port);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        isAuthorising = true;
                        int tries = 0;
                        while (!isOnline && tries < 20) {
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
                            authHandler.obtainMessage(CONNECTIONERROR,
                                    new RunnableWithCommentary(onFailedAction,CONNECTIONERRORCOMMENTARY)).sendToTarget();
                        }
                    } catch (IOException | InterruptedException e) {
                        isAuthorising = false;
                        Log.d("IOException", "could not write to a socket");
                        authHandler.obtainMessage(CONNECTIONERROR,
                                new RunnableWithCommentary(onFailedAction,CONNECTIONERRORCOMMENTARY)).sendToTarget();
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            authHandler.obtainMessage(REGISTERINPROCESS,
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
                            while (!isOnline && tries < 20) {
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
                                authHandler.obtainMessage(CONNECTIONERROR,
                                        new RunnableWithCommentary(onFailedAction, CONNECTIONERRORCOMMENTARY)).sendToTarget();
                                isAuthorising=false;
                            }
                        } catch (InterruptedException | IOException ie) {
                            authHandler.obtainMessage(CONNECTIONERROR,
                                    new RunnableWithCommentary(onFailedAction, CONNECTIONERRORCOMMENTARY)).sendToTarget();
                            ie.printStackTrace();
                            isAuthorising=false;
                        }
                    } else{
                        prepareDialogSystem(playerAuthorise.nickname);
                        authHandler.obtainMessage(AUTHORISE,
                                new RunnableWithCommentary(onSucessfullAction,"someinfo"));
                        isAuthorising=false;
                    }
                }
            }).start();
        }else{
            authHandler.obtainMessage(AUTHORISEINPROCESS,
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
                        while (!isOnline && tries < 20) {
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
                            authHandler.obtainMessage(CONNECTIONERROR,
                                    new RunnableWithCommentary(onFailedAction, CONNECTIONERRORCOMMENTARY)).sendToTarget();
                        }
                    } catch (IOException | InterruptedException e) {
                        runnableStorage.put("exit", null);
                        e.printStackTrace();
                        authHandler.obtainMessage(CONNECTIONERROR,
                                new RunnableWithCommentary(onFailedAction, CONNECTIONERRORCOMMENTARY)).sendToTarget();
                    }

                }else {
                    authHandler.obtainMessage(LOGOUTINPROCESS,
                            new RunnableWithCommentary(onFailedAction,"Log out already in process")).sendToTarget();
                    Log.d("LOGOUT", "INPROCESS");
                }
            }
        }).start();
    }


    private void cacheUser(final AuthorisationAnswer authAnswer){
        Log.d("Answer",authAnswer.nickname);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1000,0,0,0,0,0);
        cachedUsersDB.execSQL("INSERT INTO " + LoggedUsersContract.LoggedUsers.tableName +
                " VALUES (\'" +  authAnswer.nickname +"\', \'" + authAnswer.connectionKey + "\',\'" +
                calendar.toString()+"\');");
        prepareDialogSystem(authAnswer.nickname);
    }

    private void unauthoriseUser(String nickname){
        cachedUsersDB.execSQL("DELETE FROM " + LoggedUsersContract.LoggedUsers.tableName + " WHERE "+
                LoggedUsersContract.LoggedUsers.nickname+"="+"'"+nickname+"'");
        cachedUsersDB.execSQL("DELETE FROM " + LoggedUsersContract.UserMessages.tableName + " WHERE "+
                LoggedUsersContract.UserMessages.nicknameSender+"="+"'"+nickname+"'");
        Log.d("UnauthAnswer","Unauthorisation completed");
        currentUser=null;
    }

    private boolean isAuthorised(String nickname){
        Cursor cursor = cachedUsersDB.query(LoggedUsersContract.LoggedUsers.tableName,
                new String[]{LoggedUsersContract.LoggedUsers.nickname,LoggedUsersContract.LoggedUsers.connectionKey},
                LoggedUsersContract.LoggedUsers.nickname + "=" + "'" + nickname + "'",null,null,
                null,null);
        if(cursor.getCount()==0){
            cursor.close();
            return false;
        }
        else {
            cursor.close();
            return true;
        }
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
        cursor.close();
        return cachedUsers.toArray(new String[cachedUsers.size()]);
    }

//____________________________________________Dialogue and collocation methods____________________________________________________________

    public void prepareDialogSystem(final String nickname){//TODO place in secondary thread

                lastCachedMessages = new TreeMap<String, CommonClasses.Message>();
                Cursor cursor = cachedUsersDB.query(LoggedUsersContract.LoggedUsers.tableName,
                        new String[]{LoggedUsersContract.LoggedUsers.connectionKey},
                        LoggedUsersContract.LoggedUsers.nickname + "=" + "\'" + nickname + "\'",null,null,null,null);
                cursor.moveToNext();
                int key = cursor.getInt(0);
                currentUser = new User(nickname,key);
                cursor = cachedUsersDB.query(LoggedUsersContract.UserMessages.tableName,
                        new String[]{LoggedUsersContract.UserMessages.nicknameSender,LoggedUsersContract.UserMessages.message,LoggedUsersContract.UserMessages.date},
                        LoggedUsersContract.UserMessages.nicknameReceiver + "=" + "\'" + nickname + "\'",
                        null,
                        LoggedUsersContract.UserMessages.nicknameSender,
                        "Max(" + LoggedUsersContract.UserMessages.date +")",null);
                while(cursor.moveToNext()){
                    lastCachedMessages.put(cursor.getString(0),
                            new CommonClasses.Message(cursor.getString(0),nickname,
                                    cursor.getString(1),cursor.getString(2)));
                }
                cursor.close();
                cursor = cachedUsersDB.query(LoggedUsersContract.UserMessages.tableName,
                        new String[]{LoggedUsersContract.UserMessages.nicknameReceiver,
                                LoggedUsersContract.UserMessages.message,LoggedUsersContract.UserMessages.date},
                        LoggedUsersContract.UserMessages.nicknameSender + "=" + "\'" + nickname +"\'",
                        null,LoggedUsersContract.UserMessages.nicknameReceiver,
                                "MAX(" + LoggedUsersContract.UserMessages.date +")",null);
                while(cursor.moveToNext()){
                    if(!lastCachedMessages.containsKey(cursor.getString(0))) {
                        lastCachedMessages.put(cursor.getString(0),
                                new CommonClasses.Message(cursor.getString(0), nickname,
                                        "This user hasn't replied yet", "1000-00-00 00:00:00"));
                    }
                    //It is normal that receiver is places as a sender here:this message wil be shown only,so
                    //at the UI side it will look as message to this user sent, but user hasn't replied yet
                }
                isPrepared = true;

        //should be used instead of switchCurrentUser();
        //instead of just initializing user it would also initialize observerList
    }

    public void prepareCollocationSystem(){ //TODO place in secondary thread
        cachedCollocation = new ArrayList<CommonClasses.Message>();
        Cursor cursor = cachedUsersDB.query(LoggedUsersContract.UserMessages.tableName,
                new String[]{LoggedUsersContract.UserMessages.nicknameSender,
                        LoggedUsersContract.UserMessages.nicknameReceiver,LoggedUsersContract.UserMessages.message,
                LoggedUsersContract.UserMessages.date},
                "(" + LoggedUsersContract.UserMessages.nicknameSender +"="+"\'" + currentCollocutor +"\'" + " AND "
                        + LoggedUsersContract.UserMessages.nicknameReceiver + "=" +"\'" + currentUser.nickname+"\'" +")"
                +" OR (" + LoggedUsersContract.UserMessages.nicknameReceiver+"="+"\'"+currentCollocutor+"\'" +
                " AND " + LoggedUsersContract.UserMessages.nicknameSender+"="+"\'"+currentUser.nickname+"\')",
                null,null,null,
                LoggedUsersContract.UserMessages.date);
        Log.d("Prepare","collocation");
        while(cursor.moveToNext()){
            Log.d("Prepare","while");
            Log.d("Date",cursor.getString(3));
            cachedCollocation.add(new CommonClasses.Message(cursor.getString(0),
                    cursor.getString(1),cursor.getString(2),cursor.getString(3)));
        }
        Log.d("Prepare","cursor ended");
    }

    public NotifiableTreeMap<String,CommonClasses.Message> getNotifiableLastMessages(){
        notifiableLastMessages = new NotifiableTreeMap<String,CommonClasses.Message>(dialogueHandler);
        mutatorOfNotifiableTreeMap = notifiableLastMessages.getMutator();
        return notifiableLastMessages;
    }

    public NotifiableArrayList<CommonClasses.Message> getNotifiableCollocation(){
        notifiableCollocation = new NotifiableArrayList<CommonClasses.Message>(dialogueHandler);
        mutatorOfNotifiableCollocation = notifiableCollocation.getMutator();
        return notifiableCollocation;
    }

    public void startDialogueSystem(){
        mutatorOfNotifiableTreeMap.setTreeMap(lastCachedMessages);
        if(!isOnline){
            establishConnection(ngrokHost,port);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int tries = 0;
                    while (!isOnline && tries < 20) {
                        Thread.sleep(100);
                        tries++;
                    }
                    if(isOnline){
                        Cursor cursor = cachedUsersDB.query(LoggedUsersContract.LoggedUsers.tableName,
                                new String[]{LoggedUsersContract.LoggedUsers.date},
                                LoggedUsersContract.LoggedUsers.nickname + " = " + "\'" + currentUser.nickname +"\'",
                                null,null,null,null);
                        cursor.moveToNext();
                        Calendar calendar = Calendar.getInstance(cursor.getString(0));
                        Log.d("Date:",cursor.getString(0));
                        serverOutputStream.writeObject(new RequestMessagesQuery(currentUser.connectionKey,currentUser.nickname,
                                calendar));
                        serverOutputStream.flush();
                        cursor.close();
                    }else{
                        Log.d("Dialogue system","Info could not be gotten:connection lost");
                    }
                }catch (IOException | InterruptedException e){
                    Log.d("Dialogue system","Info could not be gotten:connection lost");
                }
            }
        }).start();
    }

    public void startCollocationSystem(){
        mutatorOfNotifiableCollocation.set(cachedCollocation);
    }

    public void leaveDialogSystem(){
        if(!isOnline) establishConnection(ngrokHost,port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int tries=0;
                    while(tries<20 && !isOnline){
                        tries++;
                        Thread.sleep(100);
                    }
                    if(!isOnline) {
                        serverOutputStream.writeObject(new Query(currentUser.connectionKey, currentUser.nickname, "stopGettingMessages"));
                        Log.d("Try","succesfull");
                        currentUser=null;
                        mutatorOfNotifiableTreeMap.setTreeMap(null);
                        for (String s:runnableStorage.keySet()) {
                            runnableStorage.put(s,null);
                        }
                        diceResultRunnables = null;
                    }
                }catch (IOException |InterruptedException e){
                    e.printStackTrace();
                    Log.d("Leave dialog system","action failed");
                }
            }
        }).start();
    }

    public void searchCollocutor(final String collocutor, final Runnable onSuccess, final Runnable onFault){
        if(!isOnline) establishConnection(ngrokHost,port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int tries = 0;
                    while (!isOnline && tries < 20) {
                        tries++;
                        Thread.sleep(100);
                    }
                    if(isOnline && runnableStorage.get("Collocutor search")==null){
                        runnableStorage.put("Collocutor search",new Pair<Runnable, Runnable>(onSuccess,onFault));
                        CollocutorQuery collocutorQuery = new CollocutorQuery(currentUser.connectionKey,
                                currentUser.nickname,collocutor);
                        serverOutputStream.writeObject(collocutorQuery);
                    }else{
                        if(!isOnline) {
                            Log.d("searchCollocutor", "Connection lost");
                            dialogueHandler.obtainMessage(-5,
                                    new Pair<Runnable,String>(onFault,"Connection lost")).sendToTarget();
                            runnableStorage.put("Collocutor search",null);
                        }
                        if(runnableStorage.get("Collocutor search")!=null){
                            Log.d("searchCollocutor","collocutor already in search");
                            dialogueHandler.obtainMessage(-5,
                                    new Pair<Runnable,String>(onFault,"Collocutor already in search")).sendToTarget();
                        }
                    }
                }catch (InterruptedException | IOException e){
                    Log.d("Connection","Connection lost");
                    dialogueHandler.obtainMessage(-5,
                            new Pair<Runnable,String>(onFault,"Connection lost")).sendToTarget();
                    runnableStorage.put("Collocutor search",null);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(final String message, final Runnable onFailure){
        if(!isOnline) establishConnection(ngrokHost,port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                int tries=0;
                while(!isOnline && tries<20){
                    Thread.sleep(100);
                    tries++;
                }
                if(isOnline){
                    Log.d("Message",message);
                    Log.d("Message",currentUser.nickname);
                    Log.d("Message",Integer.toString(currentUser.connectionKey));
                    serverOutputStream.writeObject(new MessageQuery(currentUser.connectionKey,currentUser.nickname,
                            currentCollocutor,message));
                    Log.d("Message","sent");
                }else{
                    Log.d("Connection","Lost");
                    dialogueHandler.obtainMessage(-5,
                            new Pair<Runnable,String>(onFailure,"Connection lost")).sendToTarget();
                }
            }catch (InterruptedException | IOException e){
                    Log.d("Connection","lost");
                    dialogueHandler.obtainMessage(-5,
                            new Pair<Runnable,String>(onFailure,"Connection lost")).sendToTarget();
                }
            }
        }).start();
    }

    public void clearCollocationResources(){
        currentCollocutor=null;
        mutatorOfNotifiableCollocation.set(null);
    }

    private void addMessageToDB(CommonClasses.Message message){//TODO place in thread
        try {
            cachedUsersDB.execSQL("INSERT INTO " + LoggedUsersContract.UserMessages.tableName + " VALUES (\'" +
                    message.sender + "\'," + "\'" + message.receiver + "\'," + "\'" +
                    message.getDate() + "\'," + "\'" + message.message + "\');");
            cachedUsersDB.execSQL("UPDATE "+LoggedUsersContract.LoggedUsers.tableName + " SET " +
                LoggedUsersContract.LoggedUsers.date + " = " + "\'"+message.date.toString() +"\'" + " WHERE " +
                LoggedUsersContract.LoggedUsers.nickname + " = " + "\'" + message.receiver +"\'" +
                    " OR " + LoggedUsersContract.LoggedUsers.nickname + " = " + "\'" + message.sender + "\';");
        Log.d("Current user",Boolean.toString(mutatorOfNotifiableTreeMap.getData(currentUser.nickname)==null));
        if(!mutatorOfNotifiableTreeMap.containsKey(message.sender) && !message.sender.equals(currentUser.nickname)){
            mutatorOfNotifiableTreeMap.addOrChangeItem(message.sender,new CommonClasses.Message(message));
        }
        else if(!message.sender.equals(currentUser.nickname) &&
                    mutatorOfNotifiableTreeMap.getData(message.sender).date.before(message.date)) {
                mutatorOfNotifiableTreeMap.addOrChangeItem(message.sender,new CommonClasses.Message(message));
        }
        if(message.sender.equals(currentUser.nickname) && !notifiableLastMessages.containsKey(message.receiver)){
            mutatorOfNotifiableTreeMap.addOrChangeItem(message.receiver,new CommonClasses.Message(message.receiver,currentUser.nickname
            ,"This user hasn't replied yet","1000-00-00 00:00:00"));
        }
        if(message.sender.equals(currentCollocutor) || message.receiver.equals(currentCollocutor)){
            mutatorOfNotifiableCollocation.add(message);
        }
        }catch (SQLiteConstraintException e){
            e.printStackTrace();
            Log.d("SQLite","You have tried to add duplicate value. Usually this means something bad,but not here,right?");
        }
    }

    public void setCurrentCollocutor(String nickname){
        currentCollocutor = nickname;
    }

//_____________________________________________Dice roll system______________________________________________________________

    public void startBattle(final String collocutor, final boolean isSender, final RunnableWithResource<Integer> onSuccess,
                            final RunnableWithResource<String> onFault){
        if(!isOnline) establishConnection(ngrokHost,port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int tries = 0;
                    while (!isOnline && tries < 20) {
                        tries++;
                        Thread.sleep(100);
                    }
                    if(isOnline && diceResultRunnables == null){
                        String operation;
                        if(isSender) {
                            operation = "startBattle";
                        }
                        else {
                            operation = "battleResponse";
                        }
                        diceResultRunnables = new Pair<>(null,null);
                        serverOutputStream.writeObject(new StartBattleQuery(currentUser.connectionKey,currentUser.nickname,
                                collocutor,true,operation));
                        diceResultRunnables.firstObj=onSuccess;
                        diceResultRunnables.secondObj=onFault;
                    }else if(!isOnline){
                        Log.d("start Battle","Connection lost");
                        dialogueHandler.obtainMessage(-10,
                                new Pair<RunnableWithResource,String>(onFault,"Connection lost")).sendToTarget();
                        diceResultRunnables = null;
                    }else {
                        dialogueHandler.obtainMessage(-10,
                                new Pair<RunnableWithResource,String>(onFault,"Game request already sent"));
                    }
                }catch(InterruptedException | IOException e){
                    e.printStackTrace();
                    dialogueHandler.obtainMessage(-10,
                            new Pair<RunnableWithResource,String>(onFault,"Connection lost")).sendToTarget();
                    diceResultRunnables = null;
                }
            }
        }).start();
    }

    public void declineBattle(final String collocutor){
        if(!isOnline) establishConnection(ngrokHost,port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    int tries=0;
                    while(!isOnline && tries<20){
                        tries++;
                        Thread.sleep(100);
                    }
                    if(isOnline){
                        serverOutputStream.writeObject(new StartBattleQuery(currentUser.connectionKey,currentUser.nickname,
                                collocutor,false,"battleResponse"));
                    }else{
                        dialogueHandler.obtainMessage(-20,"Connection lost").sendToTarget();
                    }
                }catch (InterruptedException | IOException e){
                    e.printStackTrace();
                    dialogueHandler.obtainMessage(-20,"Connection lost").sendToTarget();
                }
            }
        }).start();
    }

    public void setDiceRequestRunnable(RunnableWithResource<String> runnableWithResource){
        this.diceRequestRunnable = runnableWithResource;
    }


//_______________________________________________Helper classes______________________________________________________________

        private class AuthorisationHandler extends Handler{
        AuthorisationHandler(Looper looper){
            super(looper);
        }
            @Override
            public void handleMessage(Message msg) {
                ((RunnableWithCommentary)msg.obj).runnable.run();
                Toast.makeText(appContext,((RunnableWithCommentary)msg.obj).commentary,Toast.LENGTH_LONG).show();
            }
        }

        private class DialogueHandler extends Handler{
        DialogueHandler(Looper looper){
            super(looper);
        }
            @Override
            public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ((Runnable) msg.obj).run();
                    break;
                case 10:
                    Pair<RunnableWithResource<Integer>,Integer> pairI =
                            ((Pair<RunnableWithResource<Integer>,Integer>)msg.obj);
                    pairI.firstObj.run(pairI.secondObj);
                    break;
                case -5:
                    Toast.makeText(appContext,((Pair<Runnable,String>) msg.obj).secondObj,Toast.LENGTH_LONG).show();
                    ((Pair<Runnable,String>) msg.obj).firstObj.run();
                    break;
                case -10:
                    Pair<RunnableWithResource<String>,String> pairS = (Pair<RunnableWithResource<String>,String>)msg.obj;
                    Toast.makeText(appContext,pairS.secondObj,Toast.LENGTH_LONG).show();
                    pairS.firstObj.run(pairS.secondObj);
                    break;
                case 20:
                    Pair<RunnableWithResource<Integer>,Pair<Integer,Integer>> pairDI=
                            (Pair<RunnableWithResource<Integer>,Pair<Integer,Integer>>)msg.obj;
                    pairDI.firstObj.run(pairDI.secondObj.firstObj,pairDI.secondObj.secondObj);
                    break;
                case 21:
                    Pair<RunnableWithResource<String>,String> pairRunStr =
                            (Pair<RunnableWithResource<String>,String>)msg.obj;
                    pairRunStr.firstObj.run(pairRunStr.secondObj);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msg.what);
            }
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


        private class Description{
            String nickname;
            String operation;
            Description(String nickname,String operation){
                this.nickname = nickname;
                this.operation = operation;
            }
        }
}