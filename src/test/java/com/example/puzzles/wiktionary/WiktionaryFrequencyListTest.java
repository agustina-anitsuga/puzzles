package com.example.puzzles.wiktionary;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WiktionaryFrequencyListTest {

    @Test
    public void testProcessFrequencyFile() {
        try {
            String inputFile = "words/words.txt";
            String outputFilePath = "src/test/resources/words/test-words.txt";
            WiktionaryFrequencyList.processFrequencyFile(inputFile,outputFilePath);

            List<String> words = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(outputFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    words.add(line);
                }
            }

            assertFalse(words.isEmpty(), "The output file should not be empty");
            assertTrue(words.stream().allMatch(word -> word.length() >= 5 && !word.contains(" ")), "All words should have 5 or more characters and no spaces");

        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }
}
