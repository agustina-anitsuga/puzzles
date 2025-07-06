package com.example.puzzles.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WordSearchPositionTest {
    @Test
    public void testPositionGetters() {
        Coordinate coord = new Coordinate(4, 8);
        Direction dir = Direction.DOWN_RIGHT;
        WordSearchPosition pos = new WordSearchPosition(coord, dir);
        assertEquals(coord, pos.getCoordinate());
        assertEquals(dir, pos.getDirection());
    }
}
