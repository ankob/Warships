package com.warships.db;

import android.provider.BaseColumns;

/**
 * Created by Andrew on 01-Feb-17.
 */

public final class UserContract {
    private UserContract() {}

    public static class User implements BaseColumns{
        public static final String TABLE_NAME = "users";
        public static final String NAME = "name";
        public static final String HASH = "hash";
        public static final String WINS = "wins_number";
        public static final String DEFEATS = "defeats_number";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + User.TABLE_NAME + " ("
                + User._ID + " INTEGER PRIMARY KEY,"
                + User.NAME + " TEXT,"
                + User.WINS + " INTEGER,"
                + User.DEFEATS + " INTEGER,"
                + User.HASH + " TEXT," +
            ");";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + User.TABLE_NAME;
}
