package com.example.puzzles.acrostics;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class AcrosticPuzzleBookGeneratorTest {
    @Test
    public void testGenerateBookCreatesOutput() throws Exception {
        AcrosticPuzzleBookGenerator generator = new AcrosticPuzzleBookGenerator();
        generator.generateBook();
        File output = new File("output/PuzzleBook.docx");
        assertTrue(output.exists(), "PuzzleBook.docx should be created");
    }
}
