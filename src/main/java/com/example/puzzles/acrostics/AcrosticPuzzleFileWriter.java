package com.example.puzzles.acrostics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.PuzzleProperties;

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
        sb.append(PuzzleProperties.getProperty("label.clues"));
        sb.append("\n\n");
        int i = 1;
        for (Word word : puzzle.getWords()) {
            sb.append(i++).append(". ").append(word.getDefinition()).append("\n");
        }
        sb.append("\n");
        sb.append(PuzzleProperties.getProperty("label.characters"));
        sb.append("\n\n");
        sb.append(getSortedCharacters());

        writeToFile(sb.toString(), outputDir, fileName);
    }

    public void generateSolutionFile(String outputDir, String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(PuzzleProperties.getProperty("label.solution"));
        sb.append("\n\n");
        sb.append(PuzzleProperties.getProperty("label.phrase"));
        sb.append(" ").append(puzzle.getPhrase().getPhrase()).append("\n");        
        sb.append(PuzzleProperties.getProperty("label.source"));
        sb.append(": ").append(puzzle.getPhrase().getSource()).append("\n");
        sb.append(PuzzleProperties.getProperty("label.author"));
        sb.append(" ").append(puzzle.getPhrase().getAuthor()).append("\n\n");
        sb.append(PuzzleProperties.getProperty("label.words")).append("\n");
        int i = 1;
        for (Word word : puzzle.getWords()) {
            sb.append(i++).append(". ").append(word.getWord()).append("\n");
        }
        sb.append("\n");

        writeToFile(sb.toString(), outputDir, fileName);
    }

    public String getSortedCharacters() {
        StringBuilder sb = new StringBuilder();
        puzzle.getWords().stream()
            .flatMap(word -> word.getWord().chars().mapToObj(c -> (char) c))
            .sorted()
            .forEach(c -> sb.append(c).append(" "));
        return sb.toString().trim();
    }
}
