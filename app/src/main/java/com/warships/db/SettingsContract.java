package com.warships.db;

import android.provider.BaseColumns;

/**
 * Created by Andrew on 02-Feb-17.
 */

public final class SettingsContract {

    private SettingsContract() {}

    public static class Settings implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String PLACEMENT = "placement";
        public static final String SHOOTS = "shoots_number";
        public static final String NAME = "name";
        public static final String USER = "user";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + Settings.TABLE_NAME + " ("
                + Settings._ID + " INTEGER PRIMARY KEY,"
                + Settings.NAME + " TEXT,"
                + Settings.PLACEMENT + " TEXT,"
                + Settings.SHOOTS + " INTEGER,"
                + Settings.USER + " INTEGER," +
                " FOREIGN KEY (" + Settings.USER + ") REFERENCES " + UserContract.User.TABLE_NAME + "(" + UserContract.User._ID + ")" +
            ");";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + Settings.TABLE_NAME;
}
