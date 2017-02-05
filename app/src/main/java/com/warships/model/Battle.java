package com.warships.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Andrew on 05-Feb-17.
 */

public class Battle {

    public enum GameState { Ongoing, Win, Lose }
    public enum CellState { Unknown, Miss, Hit }

    private GameState currentGameState = GameState.Ongoing;
    Set<Point> shipsCells = new HashSet<>();
    Set<Point> shoots = new HashSet<>();
    BattleConfig config;


    public void shoot(int x, int y) {
        shoots.add(new Point(x ,y));
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

}
