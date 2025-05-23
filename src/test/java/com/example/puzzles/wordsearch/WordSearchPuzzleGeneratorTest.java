package com.example.puzzles.wordsearch;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WordSearchPuzzleGeneratorTest {
    @Test
    public void testGenerateAndPrintGrid() throws Exception {
        List<String> words = Arrays.asList("JAVA", "CODE", "PUZZLE");
        WordSearchPuzzleGenerator generator = new WordSearchPuzzleGenerator(words);
        List<com.example.puzzles.model.Word> placedWords = generator.generate();
        assertNotNull(placedWords);
        assertFalse(placedWords.isEmpty());
        // Optionally, test that all words are present in the grid
        // generator.printGrid(placedWords); // This will also generate an image
    }
}
