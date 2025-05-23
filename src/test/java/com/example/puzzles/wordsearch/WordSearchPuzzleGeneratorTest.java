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
        WordSearchPuzzleGenerator generator = new WordSearchPuzzleGenerator(words);
        Puzzle puzzle = generator.generate();
        assertNotNull(puzzle, "Puzzle should not be null");
        assertNotNull(puzzle.getWords(), "List of words should not be null");
        assertFalse(puzzle.getWords().isEmpty());
        // Optionally, test that all words are present in the grid
        // generator.printGrid(placedWords); // This will also generate an image
    }
}
