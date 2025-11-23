package com.example.puzzles.acrostics;

import org.junit.jupiter.api.Test;

import com.example.puzzles.tools.PuzzleProperties;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class AcrosticPuzzleBookGeneratorTest {
    @Test
    public void testGenerateBookCreatesOutput() throws Exception {
        AcrosticPuzzleBookGenerator generator = new AcrosticPuzzleBookGenerator();
        generator.generateBook("test1");
        File output = new File(PuzzleProperties.getProperty("puzzles.output.dir")+"/"+PuzzleProperties.getProperty("output.acrostic.book.docx"));
        assertTrue(output.exists(), "Puzzle book should be created");
    }
}
