package com.example.puzzles.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WordTest {
    @Test
    public void testGetWordAndDefinition() {
        Word word = new Word(1, "Apple", "A fruit");
        assertEquals("apple", word.getWord());
        assertEquals("A fruit", word.getDefinition());
    }

    @Test
    public void testIndexOf() {
        Word word = new Word(1, "Banana", "A fruit");
        assertEquals(1, word.indexOf('a'));
        assertEquals(-1, word.indexOf('z'));
    }

    @Test
    public void testCharAt() {
        Word word = new Word(1, "Cherry", "A fruit");
        assertEquals('c', word.charAt(0));
        assertEquals('h', word.charAt(1));
    }

    @Test
    public void testToString() {
        Word word = new Word(1, "Apple", "A fruit");
        String str = word.toString();
        assertTrue(str.contains("Apple"));
        assertTrue(str.contains("A fruit"));
    }
}
