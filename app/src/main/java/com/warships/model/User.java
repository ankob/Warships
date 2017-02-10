package com.warships.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.warships.db.UserContract;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by Andrew on 04-Feb-17.
 */

public class User {

    private long id;
    public long getId() { return id; }

    private String name;
    public String getName() { return name; }

    private static User currentUser = null;
    public static User getCurrentUser() {
        return currentUser;
    }

    private User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static void getUserFromDB(SQLiteDatabase db, String name, String pass) {
        String selection = UserContract.User.NAME + " = ? AND "
                + UserContract.User.HASH + " = ?";
        String [] selectionArgs = {
                name,
                User.calcHash(name, pass)
        };
        Cursor cursor = db.query(
                UserContract.User.TABLE_NAME,
                User.projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.getCount() == 0)
            return;
        cursor.moveToNext();
        currentUser = new User(
                cursor.getLong(cursor.getColumnIndex(UserContract.User._ID)),
                cursor.getString(cursor.getColumnIndex(UserContract.User.NAME))
        );
    }

    public static void writeNewUserToDB(SQLiteDatabase db, String name, String pass) {
        ContentValues cv = new ContentValues();

        cv.put(UserContract.User.NAME, name);
        cv.put(UserContract.User.HASH, User.calcHash(name, pass));

        db.insert(UserContract.User.TABLE_NAME, null, cv);
    }

    public static String calcHash(String name, String pass) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((pass + name + "secret").getBytes("UTF-8"));
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return null;
        }
    }

    public static final String[] projection = {
            UserContract.User._ID,
            UserContract.User.NAME,
    };

}
