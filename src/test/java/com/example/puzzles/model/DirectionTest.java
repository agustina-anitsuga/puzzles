package com.example.puzzles.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {
    @Test
    public void testDirectionValues() {
        assertEquals(0, Direction.RIGHT.dRow);
        assertEquals(1, Direction.RIGHT.dCol);
        assertEquals(-1, Direction.UP_LEFT.dRow);
        assertEquals(-1, Direction.UP_LEFT.dCol);
        assertEquals(1, Direction.DOWN_LEFT.dRow);
        assertEquals(-1, Direction.DOWN_LEFT.dCol);
    }

    @Test
    public void testAllDirectionsPresent() {
        assertEquals(8, Direction.values().length);
    }
}
