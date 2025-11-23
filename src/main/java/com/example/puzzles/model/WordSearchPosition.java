package com.example.puzzles.model;

public class WordSearchPosition implements Position{
    private Coordinate coordinate;
    private Direction direction;

    public WordSearchPosition(){}

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
