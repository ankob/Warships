package com.warships.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Andrew on 05-Feb-17.
 */

public class Battle {

    public enum GameState { Ongoing, Win, Lose }
    public enum CellState { Unknown, Miss, Hit }

    private GameState currentGameState = GameState.Ongoing;
    private HashSet<Ship> ships = new HashSet<>();
    private Set<Point> shipsCells = new HashSet<>();
    private Set<Point> shoots = new HashSet<>();
    private BattleConfig config;


    public BattleConfig getConfig() {
        return config;
    }

    public void shoot(int x, int y) {
        Point newShoot = new Point(x ,y);
        if (shoots.contains(newShoot))
            return;
        shoots.add(newShoot);
        if (shoots.containsAll(shipsCells))
            currentGameState = GameState.Win;
        else if (shoots.size() == config.getMaxShootsNumber())
            currentGameState = GameState.Lose;
    }
    public CellState[][] getBattlefield() {
        int fieldSize = config.getFieldSize();
        CellState [][] result = new CellState[fieldSize][fieldSize];
        Point buf = new Point(0, 0);
        for (int x = 0; x < fieldSize; x++)
            for (int y = 0; y < fieldSize; y ++) {
                buf.x = x;
                buf.y = y;
                if (!shoots.contains(buf))
                    result[x][y] = CellState.Unknown;
                else {
                    result[x][y] = shipsCells.contains(buf) ? CellState.Hit : CellState.Miss;
                }
            }
        return result;
    }
    public GameState getGameState() { return currentGameState; }

    private enum Direction { UP, DOWN, LEFT, RIGHT }
    private class ShipPlacementStruct{
        public int x;
        public int y;
        public Direction direction;

        public ShipPlacementStruct(int x, int y, Direction direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }
    }

    public Battle(BattleConfig bc) {
        this.config = bc;

        ArrayList<ShipPlacementStruct> availableCombinations = new ArrayList<>(
                config.getFieldSize()*config.getFieldSize()*4
        );
        boolean filled = false;
        for (int x = 0; x < config.getFieldSize(); x++)
            for (int y = 0; y < config.getFieldSize(); y++)
                for (Direction dir : Direction.values())
                    availableCombinations.add(new ShipPlacementStruct(
                            x, y, dir
                    ));
        Ship testShip;
        List<Integer> shipsSizes = config.getShipsPlacement();
        while (!filled) {
            Collections.shuffle(availableCombinations);
            ships.clear();
            for (ShipPlacementStruct sp : availableCombinations) {
                testShip = new Ship(
                        sp.direction == Direction.UP ? sp.y - shipsSizes.get(ships.size()) : sp.y,
                        sp.direction == Direction.DOWN ? sp.y + shipsSizes.get(ships.size()) : sp.y,
                        sp.direction == Direction.LEFT ? sp.x - shipsSizes.get(ships.size()) : sp.x,
                        sp.direction == Direction.RIGHT ? sp.x + shipsSizes.get(ships.size()) : sp.x
                );
                if (checkShipPlace(testShip)) {
                    addShipUnsafe(testShip);
                }
                filled = ships.size() == shipsSizes.size();
                if (filled) {
                    break;
                }
            }
        }
        for (Ship s: ships) shipsCells.addAll(s.getUnitArea());
    }

    private void addShipUnsafe(Ship ship) {
        ships.add(ship);
    }

    public boolean checkShipPlace(Ship ship) {
        int top = ship.getTop();
        int bottom = ship.getBottom();
        int left = ship.getLeft();
        int right = ship.getRight();
        if (top < 0 || bottom < 0 || left < 0 || right < 0) {
            return false;
        }
        if (top != bottom && left != right) {
            return false;
        }
        if (top > bottom || right > left) {
            return false;
        }
        if (bottom >= config.getFieldSize() || right >= config.getFieldSize()) {
            return false;
        }
        boolean isIntersection = false;
        Ship newShip = new Ship(top, bottom, right, left);
        for (Ship s: ships) {
            isIntersection = isIntersection || s.isIntersecting(newShip);
        }
        return !isIntersection;
    }

    public int getShootsNumber() {
        return shoots.size();
    }

    public int getDamage() {
        HashSet<Point> buf  = new HashSet<>();
        for (Point p: shoots)
            if (shipsCells.contains(p))
                buf.add(p);
        return buf.size();
    }

    public int getTotalShipsCellsNumber() {
        return shipsCells.size();
    }

    public void showBattlefield () {
        shoots.clear();
        for (int x = 0; x < config.getFieldSize(); x++)
            for (int y = 0; y < config.getFieldSize(); y++)
                shoots.add(new Point(x, y));
    }
}
