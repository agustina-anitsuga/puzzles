package com.example.puzzles.acrostics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;

public class AcrosticPuzzleFileWriter {

    private static final Logger logger = LogManager.getLogger(AcrosticPuzzleFileWriter.class);

    private Puzzle puzzle;

    public AcrosticPuzzleFileWriter( Puzzle puzzle ) {
        this.puzzle = puzzle;
    }

    private void writeToFile(String content, String outputDir, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputDir + File.separator + fileName))) {
            writer.write(content);
        } catch (IOException e) {
            logger.error("Error writing file: " + fileName + " - " + e.getMessage(), e);
        }
    }

    public void generateClueFile(String outputDir, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Clues\n\n");
        int i = 1;
        for (Word word : puzzle.getWords()) {
            sb.append(i++).append(". ").append(word.getDefinition()).append("\n");
        }
        sb.append("\n");
        sb.append("Letters:\n\n");
        sb.append(getSortedLetters());

        writeToFile(sb.toString(), outputDir, fileName);
    }

    public void generateSolutionFile(String outputDir, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Solution\n\n");
        sb.append("Phrase: ").append(puzzle.getPhrase().getPhrase()).append("\n");
        sb.append("Book: ").append(puzzle.getPhrase().getBook()).append("\n");
        sb.append("Author: ").append(puzzle.getPhrase().getAuthor()).append("\n\n");
        sb.append("Words:\n");
        int i = 1;
        for (Word word : puzzle.getWords()) {
            sb.append(i++).append(". ").append(word.getWord()).append("\n");
        }
        sb.append("\n");

        writeToFile(sb.toString(), outputDir, fileName);
    }

    public String getSortedLetters() {
        StringBuilder sb = new StringBuilder();
        puzzle.getWords().stream()
            .flatMap(word -> word.getWord().chars().mapToObj(c -> (char) c))
            .sorted()
            .forEach(c -> sb.append(c).append(" "));
        return sb.toString().trim();
    }
}
