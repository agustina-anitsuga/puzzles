package com.example.puzzles.model;

public class WordSearchPosition implements Position{
    private final Coordinate coordinate;
    private final Direction direction;

    public WordSearchPosition(Coordinate coordinate, Direction direction) {
        this.coordinate = coordinate;
        this.direction = direction;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public Direction getDirection() {
        return direction;
    }
}
