package com.example.puzzles.wordsearch;

import org.junit.jupiter.api.Test;

import com.example.puzzles.model.Puzzle;

import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WordSearchPuzzleGeneratorTest {
    @Test
    public void testGenerateAndPrintGrid() throws Exception {
        List<String> words = Arrays.asList("JAVA", "CODE", "PUZZLE");
        WordSearchPuzzleGenerator generator = new WordSearchPuzzleGenerator(words,25);
        Puzzle puzzle = generator.generate();
        assertNotNull(puzzle, "Puzzle should not be null");
        assertNotNull(puzzle.getWords(), "List of words should not be null");
        assertFalse(puzzle.getWords().isEmpty());
        // Optionally, test that all words are present in the grid
        // generator.printGrid(placedWords); // This will also generate an image
    }

    @Test
    public void testReadWordsFromFile() throws Exception {
        java.io.File temp = java.io.File.createTempFile("words", ".txt");
        java.nio.file.Files.write(temp.toPath(), "ONE\nTWO\nTHREE\n".getBytes());
        List<String> words = WordSearchPuzzleGenerator.readWordsFromFile(temp.getAbsolutePath());
        assertEquals(3, words.size());
        assertTrue(words.contains("ONE"));
        temp.delete();
    }

    @Test
    public void testCanPlaceWordRejectsOutOfBounds() {
        WordSearchPuzzleGenerator generator = new WordSearchPuzzleGenerator(Arrays.asList("TOOLONG"), 5);
        // Try to place a word that is too long for the grid
        assertFalse(generator.canPlaceWord("TOOLONG", 0, 0, 1, 0));
    }
}
