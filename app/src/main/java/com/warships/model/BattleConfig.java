package com.warships.model;

import java.util.HashSet;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Andrew on 04-Feb-17.
 */

public class BattleConfig {
    public static final int MAX_SHIP_LENGTH = 4;
    public static final int MIN_SHIP_LENGTH = 1;

    private int maxShipsNumber = 5;
    private int maxShootsNumber = 100;
    private int fieldSize = 8;

    HashSet<Ship> ships;

    public Battle initBattle() {
        return new Battle();
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

}

class BattleConfigEditor {
    private BattleConfig battleConfig;

    public class FormattingError extends Exception {
        public FormattingError(String msg) { super(msg); }
    }

    /**
     * Creates new ship for given settings. Throws exception if placement is wrong or
     * ships limit is exhausted. Coordinates count from top to bottom, from left to right
     * starting from 0
     */
    public void addShip(int top, int bottom, int left, int right) throws FormattingError{
        if (battleConfig.ships.size() == battleConfig.getMaxShootsNumber())
            throw new FormattingError("Ships limit exhausted, try remove existing ship.");
        if (top < 0 || bottom < 0 || left < 0 || right < 0)
            throw new FormattingError("Coordinates could not be negative.");
        if (top != bottom && left != right)
            throw new FormattingError("Ship has to be oriented horizontally or vertically.");
        if (top < bottom || right < left)
            throw new FormattingError("Wrong coordinates. Top must be less or equal to bottom, left must be less or equal to right.");
        if (bottom - top + 1 > BattleConfig.MAX_SHIP_LENGTH || right - left + 1 > BattleConfig.MAX_SHIP_LENGTH)
            throw new FormattingError("Ship is too long.");
        if (bottom - top + 1 < BattleConfig.MIN_SHIP_LENGTH || right - left + 1 < BattleConfig.MIN_SHIP_LENGTH)
            throw new FormattingError("Ship is too small.");
        boolean isIntersection = false;
        Ship newShip = new Ship(top, bottom, right, left);
        for (Ship s: battleConfig.ships) {
            isIntersection = isIntersection || s.isIntersecting(newShip);
        }
        if (isIntersection)
            throw new FormattingError("Wrong coordinates. Ship intersects with another one.");
        battleConfig.ships.add(newShip);
    }

    public void removeShipAt(int x, int y) {
        Ship shipToDelete = null;
        AreaCell selectedCell = new AreaCell(x, y);
        for (Ship s: battleConfig.ships)
            if (s.isIntersecting(selectedCell))
                shipToDelete = s;
        if (shipToDelete != null)
            battleConfig.ships.remove(shipToDelete);
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

    /**
     * Naive constructor without any checks. For secure ship creation there is `addShip` method.
     */
    Ship(int top, int bottom, int left, int right) {
        ship = new HashSet<>();
        for(int x = left; x <= right; x++)
            for(int y = top; y <= bottom; y++)
                ship.add(new Point(x, y));
    }

    public boolean isIntersecting(Unit that){
        return ((HashSet<Point>) this.ship.clone()).retainAll(that.getUnitArea());
    }

    @Override
    public Set<Point> getUnitArea() { return Collections.unmodifiableSet(ship); }
}
