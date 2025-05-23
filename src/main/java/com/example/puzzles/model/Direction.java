package com.example.puzzles.model;

public enum Direction {
    RIGHT(0, 1),
    DOWN(1, 0),
    DOWN_RIGHT(1, 1),
    UP_RIGHT(-1, 1),
    LEFT(0, -1),
    UP(-1, 0),
    UP_LEFT(-1, -1),
    DOWN_LEFT(1, -1);

    public final int dRow;
    public final int dCol;
    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }
}
