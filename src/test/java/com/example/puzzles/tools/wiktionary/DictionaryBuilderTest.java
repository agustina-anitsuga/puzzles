package com.example.puzzles.tools.wiktionary;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DictionaryBuilderTest {
    @Test
    public void testBuildDictionaryCreatesFile() throws IOException {
        String inputFilePath = "src/test/resources/words/test-words.txt";
        String outputFilePath = "src/test/resources/words/test-dictionary.txt";
        DictionaryBuilder.buildDictionary(inputFilePath, outputFilePath);
        File file = new File(outputFilePath);
        assertTrue(file.exists(), "Dictionary file should be created");
        // Clean up
        file.delete();
    }
}
