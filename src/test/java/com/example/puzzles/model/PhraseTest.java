package com.example.puzzles.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PhraseTest {
    @Test
    public void testGetCharactersInPhrase() {
        Phrase phrase = new Phrase(1, "Hello, World!", "Book", "Author");
        String chars = phrase.getCharactersInPhrase();
        assertEquals("helloworld", chars);
    }

    @Test
    public void testChunks() {
        Phrase phrase = new Phrase(1, "This is a very long phrase that should be chunked into two parts for testing purposes.", "Book", "Author");
        assertTrue(phrase.getChunks().size() >= 1);
    }

    @Test
    public void testChunkCount() {
        Phrase phrase = new Phrase(1, "Short phrase", "Book", "Author");
        assertEquals(1, phrase.chunkCount());
    }

    @Test
    public void testToCharArray() {
        Phrase phrase = new Phrase(1, "abc", "Book", "Author");
        char[] chars = phrase.toCharArray();
        assertArrayEquals(new char[]{'a','b','c'}, chars);
    }

    @Test
    public void testCharAt() {
        Phrase phrase = new Phrase(1, "abc", "Book", "Author");
        assertEquals('a', phrase.charAt(0));
        assertEquals('b', phrase.charAt(1));
        assertEquals('c', phrase.charAt(2));
    }
}
