package com.example.puzzles.wordsearch;

import com.example.puzzles.tools.WordReader;
import com.example.puzzles.model.WordSearchPuzzle;
import com.example.puzzles.tools.PuzzleProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a book of word search puzzles from a text file using WordReader.
 */
public class WordSearchPuzzleBookGenerator {

    private final String wordListFilePath;
    private final int gridSize;
    private final String outputDir;

    public WordSearchPuzzleBookGenerator(String wordListFilePath, int gridSize, String outputDir) {
        this.wordListFilePath = wordListFilePath;
        this.gridSize = gridSize;
        this.outputDir = outputDir;
    }

    /**
     * Generates all puzzles in the book and writes their images to the output directory.
     * @return list of generated WordSearchPuzzle objects
     */
    public void generateBook() throws Exception {
        // read list of words
        WordReader reader = new WordReader(wordListFilePath);
        // generate puzzles
        List<WordSearchPuzzle> puzzles = getPuzzles(reader);
        // write book
        WordSearchPuzzleBookDocumentWriter writer = new WordSearchPuzzleBookDocumentWriter();
        writer.writeBook(puzzles, outputDir+File.separator+"word-search-puzzles.docx");
        System.out.println("Word search puzzles generated and saved to " + outputDir);
    }

    private List<WordSearchPuzzle> getPuzzles(WordReader reader) {
        List<WordSearchPuzzle> puzzles = new ArrayList<>();
        for (int i = 0; i < reader.getPuzzleCount(); i++) {
            List<String> words = reader.getWordList(i);
            WordSearchPuzzleGenerator generator = new WordSearchPuzzleGenerator(words, gridSize);
            try {
                WordSearchPuzzle puzzle = generator.generate();
                puzzles.add(puzzle);
            } catch (Exception e) {
                System.err.println("Failed to generate puzzle " + (i+1) + ": " + e.getMessage());
            }
        }
        return puzzles;
    }

    public static void main(String[] args) throws Exception {
        String wordListFile = PuzzleProperties.getProperty("word-search.file.path");
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");
        int gridSize = PuzzleProperties.getIntProperty("word-search.grid.size");
        WordSearchPuzzleBookGenerator bookGen = new WordSearchPuzzleBookGenerator(wordListFile, gridSize, outputDir);
        bookGen.generateBook();
        System.out.println("Generated word search puzzle book.");
    }
}
