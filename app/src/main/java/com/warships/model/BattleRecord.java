package com.warships.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.warships.db.StatContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Andrew on 10-Feb-17.
 */

public class BattleRecord {

    private long id;
    private Date time;
    private boolean win;
    private long player;
    private int moves;
    private int movesLeft;
    private int damage;
    private int shipsLeft;

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public BattleRecord(long id, String time, String win, long player, int moves, int movesLeft, int damage, int shipsLeft) {
        this.id = id;
        try {
            this.time = format.parse(time);
        } catch (ParseException e) {
            this.time = null;
        }
        this.win = Boolean.valueOf(win);
        this.player = player;
        this.moves = moves;
        this.movesLeft = movesLeft;
        this.damage = damage;
        this.shipsLeft = shipsLeft;
    }

    public static void writeRecordToDB(SQLiteDatabase db, BattleRecord br) {
        return;
    }

    public static List<BattleRecord> getRecordsFromDB(SQLiteDatabase db, long player) {
        String[] projection = {
                StatContract.Stat._ID,
                StatContract.Stat.GAME_TIME,
                StatContract.Stat.WIN,
                StatContract.Stat.PLAYER,
                StatContract.Stat.MOVES,
                StatContract.Stat.MOVES_LEFT,
                StatContract.Stat.DAMAGE,
                StatContract.Stat.SHIPS_LEFT,
        };
        String selection = StatContract.Stat.PLAYER + " = ?";
        String[] selectionArgs = { Long.toString(player) };
        Cursor cursor = db.query(
                StatContract.Stat.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
        List<BattleRecord> result = new ArrayList<>();
        while(cursor.moveToNext()) {
            result.add(new BattleRecord(
                    cursor.getLong(cursor.getColumnIndex(StatContract.Stat._ID)),
                    cursor.getString(cursor.getColumnIndex(StatContract.Stat.GAME_TIME)),
                    cursor.getString(cursor.getColumnIndex(StatContract.Stat.WIN)),
                    cursor.getLong(cursor.getColumnIndex(StatContract.Stat.PLAYER)),
                    cursor.getInt(cursor.getColumnIndex(StatContract.Stat.MOVES)),
                    cursor.getInt(cursor.getColumnIndex(StatContract.Stat.MOVES_LEFT)),
                    cursor.getInt(cursor.getColumnIndex(StatContract.Stat.DAMAGE)),
                    cursor.getInt(cursor.getColumnIndex(StatContract.Stat.SHIPS_LEFT))
            ));
        }
        return result;
    }

    public Date getTime() {
        return time;
    }

    public boolean isWin() {
        return win;
    }

    public long getPlayer() {
        return player;
    }

    public int getMoves() {
        return moves;
    }

    public int getMovesLeft() {
        return movesLeft;
    }

    public int getDamage() {
        return damage;
    }

    public int getShipsLeft() {
        return shipsLeft;
    }
}