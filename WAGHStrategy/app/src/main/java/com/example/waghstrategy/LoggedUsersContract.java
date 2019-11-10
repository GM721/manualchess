package com.example.waghstrategy;

import android.provider.BaseColumns;

public class LoggedUsersContract {

    private LoggedUsersContract(){}

    public static class LoggedUsers implements BaseColumns{
        public static final String tableName = "AuthorisedUsers";
        public static final String nickname = "Nickname";
        public static final String password = "Password";
        public static final String connectionKey = "ConnectionKey";
        public static final String  SQLCreateDB = "CREATE TABLE " + tableName + "( " +
                nickname + " TINYTEXT PRIMARY KEY," + connectionKey + " INTEGER)";
        public static final String SQLDeleteDB = "DROP TABLE IF EXISTS " + tableName;

    }
}
