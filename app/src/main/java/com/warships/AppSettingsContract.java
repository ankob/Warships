package com.warships;

import android.provider.BaseColumns;

/**
 * Created by Andrew on 02-Feb-17.
 */

public final class AppSettingsContract {

    private AppSettingsContract() {}

    public static class AppSettings implements BaseColumns {
        public static final String TABLE_NAME = "app_settings";
        public static final String CURRENT_USER = "current_user";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + AppSettings.TABLE_NAME + " ("
                + AppSettings._ID + " INTEGER PRIMARY KEY,"
                + AppSettings.CURRENT_USER + " INTEGER, " +
                "FOREIGN KEY (" + AppSettings.CURRENT_USER + ") REFERENCES " + UserContract.User.TABLE_NAME + "(" + UserContract.User._ID + ")" +
            ");";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + AppSettings.TABLE_NAME;
}

