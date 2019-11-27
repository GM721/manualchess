package manualchess.application;

import android.provider.BaseColumns;

public class LoggedUsersContract {

    private LoggedUsersContract(){}

    public static class LoggedUsers implements BaseColumns{
        public static final String tableName = "AuthorisedUsers";
        public static final String nickname = "Nickname";
        public static final String connectionKey = "ConnectionKey";
        public static final String  SQLCreateDB = "CREATE TABLE " + tableName + "( " +
                nickname + " TINYTEXT PRIMARY KEY," + connectionKey + " INTEGER)";
        public static final String SQLDeleteDB = "DROP TABLE IF EXISTS " + tableName;

    }
    public static class UserMessages implements BaseColumns{
        public static final String tableName = "MessagesOfUsers";
        public static final String nicknameSender = "NicknameOfSender";
        public static final String nicknameReceiver = "NicknameOfReceiver";
        public static final String date = "Date";
        public static final String message = "Message";
        public static final String PKMessage = "PK_Message";
        public static final String SQLCreateDB = "CREATE TABLE " + tableName + "(" +
                nicknameSender + " TINYTEXT, " + nicknameReceiver + " TINYTEXT, " + date + " DATETIME, " +
                message + " TEXT, " + "FOREIGN KEY (" + nicknameSender +") REFERENCES " +
                LoggedUsers.tableName + "(" + LoggedUsers.nickname+"), " + " CONSTRAINT " + PKMessage +
                " PRIMARY KEY " + "(" + nicknameSender + "," + nicknameReceiver + "," + date + "))";
    }

}
