package com.example.puzzles.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {
    @Test
    public void testPositionGetters() {
        Coordinate coord = new Coordinate(4, 8);
        Direction dir = Direction.DOWN_RIGHT;
        Position pos = new Position(coord, dir);
        assertEquals(coord, pos.getCoordinate());
        assertEquals(dir, pos.getDirection());
    }
}
