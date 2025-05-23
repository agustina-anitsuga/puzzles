package com.example.puzzles.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoordinateTest {
    @Test
    public void testCoordinateGetters() {
        Coordinate coord = new Coordinate(5, 7);
        assertEquals(5, coord.getRow());
        assertEquals(7, coord.getCol());
    }

    @Test
    public void testCoordinateEquality() {
        Coordinate c1 = new Coordinate(2, 3);
        Coordinate c2 = new Coordinate(2, 3);
        assertNotSame(c1, c2);
        assertEquals(c1.getRow(), c2.getRow());
        assertEquals(c1.getCol(), c2.getCol());
    }
}
