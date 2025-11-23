package com.example.puzzles.acrostics;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.FileUtils;
import com.example.puzzles.tools.PuzzleProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class AcrosticPuzzleFileWriter {

    private static final Logger logger = LogManager.getLogger(AcrosticPuzzleFileWriter.class);

    private AcrosticPuzzle puzzle;

    public AcrosticPuzzleFileWriter( AcrosticPuzzle puzzle ) {
        this.puzzle = puzzle;
    }

    public void generateClueFile(String outputDir, String fileName) throws IOException {
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

        FileUtils.writeToFile(sb.toString(), outputDir, fileName);
    }

    public void generateSolutionFile(String outputDir, String fileName) throws IOException {
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

        FileUtils.writeToFile(sb.toString(), outputDir, fileName);
    }

    public String getSortedCharacters() {
        StringBuilder sb = new StringBuilder();
        puzzle.getWords().stream()
            .flatMap(word -> word.getWord().chars().mapToObj(c -> (char) c))
            .sorted()
            .forEach(c -> sb.append(c).append(" "));
        return sb.toString().trim();
    }

    public void generatePuzzleFile(String outputDir, String fileName) throws IOException {
        StringBuffer sb = new StringBuffer();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            sb.append( mapper.writeValueAsString(puzzle) );
        } catch (JsonProcessingException e) {
            logger.error("Error writing json file: " + fileName + " - " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        FileUtils.writeToFile(sb.toString(), outputDir, fileName);
    }
}
