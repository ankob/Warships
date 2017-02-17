package com.warships.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.warships.db.SettingsContract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Andrew on 04-Feb-17.
 */

public class BattleConfig {
    public static final int MAX_SHIP_LENGTH = 4;
    public static final int MIN_SHIP_LENGTH = 1;
    public static final long NEW_CONFIG_ID = -1;

    private long id;
    private int maxShipsNumber = 5;
    private int maxShipSize = 5;
    private int maxShootsNumber = 100;
    private int fieldSize = 8;
    private boolean current;
    private String name;
    private List<Integer> shipsPlacement;

    public Battle initBattle() {
        return new Battle(this);
    }

    public long getId() {
        return id;
    }


    public int getMaxShipsNumber() {
        return maxShipsNumber;
    }

    public int getMaxShootsNumber() {
        return maxShootsNumber;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public int getMaxShipSize() {
        return maxShipSize;
    }

    public List<Integer> getShipsPlacement() {
        return shipsPlacement;
    }

    public boolean isCurrent() {
        return current;
    }

    public String getName() {
        return name;
    }

    public BattleConfig(
            long id,
            List<Integer> shipsPlacement,
            int maxShootsNumber,
            boolean current,
            String name
    ) {
        this.id = id;
        this.shipsPlacement = shipsPlacement;
        this.maxShootsNumber = maxShootsNumber;
        this.current = current;
        this.name = name;

    }

    public static List<BattleConfig> getRecordsFromDB(SQLiteDatabase db, long player) {
        String[] projection = {
                SettingsContract.Settings._ID,
                SettingsContract.Settings.NAME,
                SettingsContract.Settings.SHOOTS,
                SettingsContract.Settings.PLACEMENT,
                SettingsContract.Settings.CURRENT
        };
        String selection = SettingsContract.Settings.USER + " = ?";
        String[] selectionArgs = { Long.toString(player) };
        Cursor cursor = db.query(
                SettingsContract.Settings.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null
        );
        List<BattleConfig> result = new ArrayList<>();
        while(cursor.moveToNext()) {
            result.add(new BattleConfig(
                    cursor.getLong(cursor.getColumnIndex(SettingsContract.Settings._ID)),
                    textToShipsPlacement(cursor.getString(cursor.getColumnIndex(SettingsContract.Settings.PLACEMENT))),
                    cursor.getInt(cursor.getColumnIndex(SettingsContract.Settings.SHOOTS)),
                    cursor.getInt(cursor.getColumnIndex(SettingsContract.Settings.CURRENT)) == 1,
                    cursor.getString(cursor.getColumnIndex(SettingsContract.Settings.NAME))
            ));
        }
        return result;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public void deleteConfig(SQLiteDatabase db) {
        String deletion = SettingsContract.Settings._ID + " = ?";
        String[] deletionArgs = { Long.toString(id) };
        db.delete(
                SettingsContract.Settings.TABLE_NAME,
                deletion,
                deletionArgs
        );
    }

    public void setMaxShootsNumber(int maxShootsNumber) {
        this.maxShootsNumber = maxShootsNumber;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setShipsPlacement(List<Integer> shipsPlacement) {
        this.shipsPlacement = shipsPlacement;
    }

    public void writeConfigToDB(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        cv.put(SettingsContract.Settings.NAME, name);
        cv.put(SettingsContract.Settings.CURRENT, current ? "1" : "0");
        cv.put(SettingsContract.Settings.SHOOTS, maxShootsNumber);
        cv.put(SettingsContract.Settings.USER, Long.toString(User.getCurrentUser().getId()));

        StringBuilder placement = new StringBuilder();
        for (Integer i: shipsPlacement) {
            placement.append(i);
            placement.append(',');
        }
        placement.deleteCharAt(placement.length() - 1); // last coma

        cv.put(SettingsContract.Settings.PLACEMENT, placement.toString());

        if (getId() == NEW_CONFIG_ID) {
            db.insert(SettingsContract.Settings.TABLE_NAME, null, cv);
        } else {
            String where = SettingsContract.Settings._ID + " = ?";
            String[] whereArgs = { Long.toString(id) };
            db.update(SettingsContract.Settings.TABLE_NAME, cv, where, whereArgs);
        }
    }

    private static List<Integer> textToShipsPlacement(String s) {
        String []sizes = s.split(",");
        LinkedList<Integer> result = new LinkedList<>();
        for (int i = 0; i < sizes.length; i++)
            result.add(Integer.parseInt(sizes[i]));
        return result;
    }
}

class Point {
    int x, y;
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() { return x + y * 100; }

    @Override
    public boolean equals(Object that) {
        if (this == that)
            return true;
        if (that == null || this.getClass() != that.getClass())
            return false;
        Point p = (Point) that;
        return this.x == p.x && this.y == p.y;
    }
}


interface Unit {

    /**
     * Method to simplify intersection checks. Responds for object's
     * immutability.
     * @return unit area to check intersection
     */
    Set<Point> getUnitArea ();

}

class AreaCell implements Unit {
    private Point position;

    AreaCell(int x, int y) {
        position = new Point(x, y);
    }

    @Override
    public Set<Point> getUnitArea() {
        return Collections.unmodifiableSet(new HashSet<>(Collections.singletonList(position)));
    }
}

class Ship implements Unit {

    private HashSet<Point> ship;
    private int top;
    private int bottom;
    private int left;
    private int right;

    /**
     * Naive constructor without any checks. For secure ship creation there is `addShip` method.
     */
    Ship(int top, int bottom, int left, int right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        ship = new HashSet<>();
        for(int x = left; x <= right; x++)
            for(int y = top; y <= bottom; y++)
                ship.add(new Point(x, y));
    }

    public boolean isIntersecting(Unit that){
        return ((HashSet<Point>) this.ship.clone()).removeAll(that.getUnitArea());
    }

    @Override
    public Set<Point> getUnitArea() { return Collections.unmodifiableSet(ship); }

    public int getRight() {
        return right;
    }

    public int getLeft() {
        return left;
    }

    public int getBottom() {
        return bottom;
    }

    public int getTop() {
        return top;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "right=" + right +
                ", left=" + left +
                ", bottom=" + bottom +
                ", top=" + top +
                '}';
    }
}
