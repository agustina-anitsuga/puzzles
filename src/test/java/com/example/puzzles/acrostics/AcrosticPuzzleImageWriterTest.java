package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.AcrosticPuzzlePosition;
import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Word;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class AcrosticPuzzleImageWriterTest {
    @Test
    public void testGenerateImageCreatesFile() {
        Phrase phrase = new Phrase(1, "abc", "Book", "Author");
        List<Word> words = List.of(
            new Word(1, "apple", "A fruit", new AcrosticPuzzlePosition(0)),
            new Word(2, "banana", "Another fruit", new AcrosticPuzzlePosition(0)),
            new Word(3, "cherry", "Yet another fruit", new AcrosticPuzzlePosition(0))
        );
        AcrosticPuzzle puzzle = new AcrosticPuzzle(LocalDateTime.now(), phrase, words);
        AcrosticPuzzleImageWriter writer = new AcrosticPuzzleImageWriter(puzzle);
        String outputDir = "src/test/resources/images";
        String fileName = "test-image.png";
        writer.generate(outputDir, fileName, true);
        File file = new File(outputDir, fileName);
        assertTrue(file.exists(), "Image file should be created");
        // Clean up
        file.delete();
    }
}
