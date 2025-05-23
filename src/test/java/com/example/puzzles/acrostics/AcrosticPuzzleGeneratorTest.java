package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.PhraseReader;
import com.example.puzzles.tools.PuzzleProperties;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class AcrosticPuzzleGeneratorTest {

    @Test
    public void testGetRandomPhrase() {
        PhraseReader phraseReader = new PhraseReader();
        Phrase phrase = phraseReader.getRandomPhrase(PuzzleProperties.getProperty("phrases.file.path"));

        assertNotNull(phrase, "Random phrase should not be null");
        assertNotNull(phrase.getSource(), "Book name should not be null");
        assertNotNull(phrase.getAuthor(), "Author name should not be null");
    }

    @Test
    public void testPuzzleCreation() {
        List<Word> words = new ArrayList<>();
        words.add(new Word(1, "apple", "A fruit"));
        words.add(new Word(2, "banana", "Another fruit"));
        words.add(new Word(3, "cherry", "Yet another fruit"));

        AcrosticPuzzle puzzle = new AcrosticPuzzle(
            LocalDateTime.now(),
            new Phrase(1,"abc","book","author"), 
            words);

        assertEquals("abc", puzzle.getPhrase().getPhrase(), "Phrase should match the input");
        assertEquals(3, puzzle.getWords().size(), "Words list should contain 3 words");
        assertTrue(puzzle.getWords().containsAll(words), "Words list should contain all added words");
    }
}
