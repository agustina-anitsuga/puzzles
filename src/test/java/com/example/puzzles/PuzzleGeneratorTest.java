package com.example.puzzles;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class PuzzleGeneratorTest {

    @Test
    public void testGetRandomPhrase() {
        PhraseReader phraseReader = new PhraseReader();
        Phrase phrase = phraseReader.getRandomPhrase("phrases/Iconic_Book_Quotes.xlsx");

        assertNotNull(phrase, "Random phrase should not be null");
        assertNotNull(phrase.getBook(), "Book name should not be null");
        assertNotNull(phrase.getAuthor(), "Author name should not be null");
    }

    @Test
    public void testPuzzleCreation() {
        List<Word> words = new ArrayList<>();
        words.add(new Word(1, "apple", "A fruit"));
        words.add(new Word(2, "banana", "Another fruit"));
        words.add(new Word(3, "cherry", "Yet another fruit"));

        Puzzle puzzle = new Puzzle(
            LocalDateTime.now(),
            new Phrase(1,"abc","book","author"), 
            words);

        assertEquals("abc", puzzle.getPhrase().getPhrase(), "Phrase should match the input");
        assertEquals(3, puzzle.getWords().size(), "Words list should contain 3 words");
        assertTrue(puzzle.getWords().containsAll(words), "Words list should contain all added words");
    }
}
