package com.example.puzzles.acrostics;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AcrosticPuzzleFileWriterTest {

    @Test
    public void testGetSortedLetters() {
        // Arrange
        List<Word> words = List.of(
            new Word(1, "hi there joe! aaah", "A greeting"),
            new Word(2, "love vinyl", "A material")
        );
        Phrase phrase = new Phrase(1, "hello", "book", "author");
        Puzzle puzzle = new Puzzle(LocalDateTime.now(), phrase, words);
        AcrosticPuzzleFileWriter writer = new AcrosticPuzzleFileWriter(puzzle);

        // Act
        String sortedLetters = writer.getSortedCharacters();

        // Assert
        assertEquals("! a a a e e e e h h h i i j l l n o o r t v v y", sortedLetters, "The sorted letters should match the expected output.");
    }
}
