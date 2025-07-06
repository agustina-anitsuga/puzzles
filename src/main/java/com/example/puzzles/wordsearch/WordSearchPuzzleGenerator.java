package com.example.puzzles.wordsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.puzzles.model.Coordinate;
import com.example.puzzles.model.Direction;
import com.example.puzzles.model.Position;
import com.example.puzzles.model.Word;
import com.example.puzzles.model.WordSearchPosition;
import com.example.puzzles.model.WordSearchPuzzle;
import com.example.puzzles.tools.PuzzleProperties;

public class WordSearchPuzzleGenerator {
    
    private static final Logger logger = LogManager.getLogger(WordSearchPuzzleGenerator.class);
    private final Random random = new Random();

    private int gridSize;
    private char grid[][];
    private List<String> words;

    public WordSearchPuzzleGenerator(List<String> words, int gridSize) {
        this.words = words;
        this.gridSize = gridSize;
        this.grid = new char[gridSize][gridSize];
        fillGridWithBlanks();
    }

    public static List<String> readWordsFromFile(String filePath) throws IOException {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.trim();
                if (!word.isEmpty()) {
                    words.add(word.toUpperCase());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading words from file: {}", filePath, e);
            throw e;
        }
        logger.info("Loaded {} words from file: {}", words.size(), filePath);
        return words;
    }

    public WordSearchPuzzle generate() throws Exception {
        List<Word> placedWords = buildGrid();
        WordSearchPuzzle wsp = new WordSearchPuzzle(LocalDateTime.now(),grid,placedWords);

        String filePath = PuzzleProperties.getProperty("puzzles.output.dir");
        this.printGrid(filePath,wsp);

        return wsp;
    }

    private List<Word> buildGrid() throws Exception {
        List<Word> placedWords = new ArrayList<>();
        int i=1;
        for (String word : words) {
            Position position = placeWord(word);
            placedWords.add(new Word(i++,word, position));
        }
        fillEmptySpaces();
        logger.info("Word search puzzle generated with {} words.", words.size());
        return placedWords;
    }

    private void fillGridWithBlanks() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                grid[i][j] = '_';
            }
        }
    }

    private void fillEmptySpaces() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (grid[i][j] == '_') {
                    grid[i][j] = (char) ('A' + random.nextInt(26));
                }
            }
        }
    }

    private Position placeWord(String word) throws Exception {
        List<Direction> directions = new ArrayList<>();
        Collections.addAll(directions, Direction.values());
        Collections.shuffle(directions, random);
        for (int attempt = 0; attempt < 100; attempt++) {
            Direction dir = directions.get(random.nextInt(directions.size()));
            int row = random.nextInt(gridSize);
            int col = random.nextInt(gridSize);
            if (canPlaceWord(word, row, col, dir.dRow, dir.dCol)) {
                for (int k = 0; k < word.length(); k++) {
                    grid[row + k * dir.dRow][col + k * dir.dCol] = word.charAt(k);
                }
                return new WordSearchPosition(new Coordinate(row,col), dir);
            }
        }
        throw new Exception( String.format("Could not place word in grid after 100 attempts: {}", word));
    }

    protected boolean canPlaceWord(String word, int row, int col, int dRow, int dCol) {
        int len = word.length();
        int endRow = row + (len - 1) * dRow;
        int endCol = col + (len - 1) * dCol;
        if (endRow < 0 || endRow >= gridSize || endCol < 0 || endCol >= gridSize) {
            return false;
        }
        for (int k = 0; k < len; k++) {
            char c = grid[row + k * dRow][col + k * dCol];
            if (c != '_' && c != word.charAt(k)) {
                return false;
            }
        }
        return true;
    }

    public void printGrid(String filePath, WordSearchPuzzle puzzle) {
        // Also write the grid as an image
        try {
            WordSearchPuzzleImageWriter writer = new WordSearchPuzzleImageWriter(puzzle);
            writer.writeToFile(filePath+File.separator+puzzle.getName()+".png", false);
            writer.writeToFile(filePath+File.separator+puzzle.getName()+"-sol.png", true);
            logger.info("Word search puzzle image written to {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to write word search puzzle image", e);
        }
    }

    public static void main(String[] args) throws Exception {
        logger.info("Welcome to the Puzzle Generator!");
        List<String> words = readWordsFromFile(PuzzleProperties.getProperty("word-search.file.path"));
        WordSearchPuzzleGenerator generator = new WordSearchPuzzleGenerator(words,25);
        generator.generate();
        logger.info("Puzzle grid printed to console.");
    }
}
