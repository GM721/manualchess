package com.example.waghstrategy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteCachedUsers extends SQLiteOpenHelper {

    

    public static final Integer dbVersion = 1;
    public static final String dbName = "CachedUsers.db";

    public SQLiteCachedUsers(@Nullable Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LoggedUsersContract.LoggedUsers.SQLCreateDB); //TODO Реализовать выполнение в отдельном потоке!!!
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
