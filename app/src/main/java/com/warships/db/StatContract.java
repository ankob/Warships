package com.warships.db;

import android.provider.BaseColumns;

/**
 * Created by Andrew on 05-Feb-17.
 */

public final class StatContract {
    private StatContract() {}

    public static class Stat implements BaseColumns {
        public static final String TABLE_NAME = "stat";
        public static final String GAME_TIME = "game_time";
        public static final String WIN = "win";
        public static final String PLAYER = "player";
        public static final String MOVES = "moves";
        public static final String MOVES_LEFT = "moves_left";
        public static final String DAMAGE = "damage";
        public static final String SHIPS_LEFT = "ships_left";
    }

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + Stat.TABLE_NAME + " ("
                + Stat._ID + " INTEGER PRIMARY KEY, "
                + Stat.WIN + " INTEGER, "
                + Stat.GAME_TIME + " TEXT, "
                + Stat.PLAYER + " INTEGER, "
                + Stat.MOVES+ " INTEGER, "
                + Stat.DAMAGE + " INTEGER, "
                + Stat.MOVES_LEFT + " INTEGER, "
                + Stat.SHIPS_LEFT + " INTEGER, "
                + " FOREIGN KEY (" + Stat.PLAYER + ") REFERENCES " + UserContract.User.TABLE_NAME + "(" + UserContract.User._ID + ")"
            + ");";
    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + Stat.TABLE_NAME;

}
