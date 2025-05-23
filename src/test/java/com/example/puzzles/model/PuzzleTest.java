package com.example.puzzles.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PuzzleTest {
    @Test
    public void testGetName() {
        Phrase phrase = new Phrase(1, "abc", "Book", "Author");
        List<Word> words = List.of(new Word(1, "apple", "A fruit"));
        AcrosticPuzzle puzzle = new AcrosticPuzzle(LocalDateTime.of(2025, 5, 16, 12, 0, 0), phrase, words);
        assertTrue(puzzle.getName().startsWith("acrostic-puzzle-2025-05-16-12-00-00"));
    }

    @Test
    public void testToString() {
        Phrase phrase = new Phrase(1, "abc", "Book", "Author");
        List<Word> words = List.of(new Word(1, "apple", "A fruit"));
        AcrosticPuzzle puzzle = new AcrosticPuzzle(LocalDateTime.now(), phrase, words);
        String str = puzzle.toString();
        assertTrue(str.contains("Words:"));
        assertTrue(str.contains("apple"));
    }
}
