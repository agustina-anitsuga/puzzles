package com.example.puzzles.wordsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
import com.example.puzzles.tools.PuzzleProperties;

public class WordSearchPuzzleGenerator {
    private static final Logger logger = LogManager.getLogger(WordSearchPuzzleGenerator.class);
    private static final int GRID_SIZE = 25;
    private final char[][] grid;
    private final List<String> words;
    private final Random random = new Random();

    public WordSearchPuzzleGenerator(List<String> words) {
        this.words = new ArrayList<>(words);
        this.grid = new char[GRID_SIZE][GRID_SIZE];
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

    public List<Word> generate() throws Exception {
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
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = '_';
            }
        }
    }

    private void fillEmptySpaces() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
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
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            if (canPlaceWord(word, row, col, dir.dRow, dir.dCol)) {
                for (int k = 0; k < word.length(); k++) {
                    grid[row + k * dir.dRow][col + k * dir.dCol] = word.charAt(k);
                }
                return new Position(new Coordinate(row,col), dir);
            }
        }
        throw new Exception( String.format("Could not place word in grid after 100 attempts: {}", word));
    }

    private boolean canPlaceWord(String word, int row, int col, int dRow, int dCol) {
        int len = word.length();
        int endRow = row + (len - 1) * dRow;
        int endCol = col + (len - 1) * dCol;
        if (endRow < 0 || endRow >= GRID_SIZE || endCol < 0 || endCol >= GRID_SIZE) {
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

    public void printGrid(List<Word> placedWords) {
        // for (int i = 0; i < GRID_SIZE; i++) {
        //    for (int j = 0; j < GRID_SIZE; j++) {
        //        System.out.print(grid[i][j] + " ");
        //    }
        //    System.out.println();
        //}
        // Also write the grid as an image
        try {
            String filePath = "wordsearch.png";
            new WordSearchPuzzleImageWriter(grid, GRID_SIZE, true, placedWords).writeToFile(filePath);
            logger.info("Word search puzzle image written to {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to write word search puzzle image", e);
        }
    }

    public static void main(String[] args) throws Exception {
        logger.info("Welcome to the Puzzle Generator!");
        List<String> words = readWordsFromFile(PuzzleProperties.getProperty("word-search.file.path"));
        WordSearchPuzzleGenerator generator = new WordSearchPuzzleGenerator(words);
        List<Word> placedWords = generator.generate();
        generator.printGrid(placedWords);
        logger.info("Puzzle grid printed to console.");
    }
}
