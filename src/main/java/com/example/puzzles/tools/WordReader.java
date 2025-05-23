package com.example.puzzles.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a text file containing multiple word lists separated by blank lines.
 * Each list is used for a separate word search puzzle.
 */
public class WordReader {
    private final List<List<String>> wordLists = new ArrayList<>();

    /**
     * Reads the file and splits it into lists of words, one per puzzle.
     * @param filePath path to the text file
     * @throws IOException if file cannot be read
     */
    public WordReader(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            List<String> currentList = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    if (!currentList.isEmpty()) {
                        wordLists.add(new ArrayList<>(currentList));
                        currentList.clear();
                    }
                } else {
                    currentList.add(trimmed.toUpperCase());
                }
            }
            if (!currentList.isEmpty()) {
                wordLists.add(currentList);
            }
        }
    }

    /**
     * Returns the list of word lists (one per puzzle).
     */
    public List<List<String>> getWordLists() {
        return wordLists;
    }

    /**
     * Returns the word list for the given puzzle index (0-based).
     */
    public List<String> getWordList(int index) {
        return wordLists.get(index);
    }

    /**
     * Returns the number of word lists (puzzles) in the file.
     */
    public int getPuzzleCount() {
        return wordLists.size();
    }
}
