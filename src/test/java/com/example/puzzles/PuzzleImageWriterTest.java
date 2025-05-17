package com.example.puzzles;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PuzzleImageWriterTest {
    @Test
    public void testGenerateImageCreatesFile() {
        Phrase phrase = new Phrase(1, "abc", "Book", "Author");
        List<Word> words = List.of(
            new Word(1, "apple", "A fruit"),
            new Word(2, "banana", "Another fruit"),
            new Word(3, "cherry", "Yet another fruit")
        );
        Puzzle puzzle = new Puzzle(LocalDateTime.now(), phrase, words);
        PuzzleImageWriter writer = new PuzzleImageWriter(puzzle);
        String outputDir = "src/test/resources/images";
        String fileName = "test-image.png";
        writer.generate(outputDir, fileName, true);
        File file = new File(outputDir, fileName);
        assertTrue(file.exists(), "Image file should be created");
        // Clean up
        file.delete();
    }
}
