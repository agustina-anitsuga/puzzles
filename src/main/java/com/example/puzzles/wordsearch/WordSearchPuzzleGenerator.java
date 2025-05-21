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

    public void generate() {
        for (String word : words) {
            placeWord(word);
        }
        fillEmptySpaces();
        logger.info("Word search puzzle generated with {} words.", words.size());
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

    private void placeWord(String word) {
        List<int[]> directions = new ArrayList<int[]>();
        directions.add(new int[]{0, 1});   // right
        directions.add(new int[]{1, 0});   // down
        directions.add(new int[]{1, 1});   // down-right
        directions.add(new int[]{-1, 1});  // up-right
        directions.add(new int[]{0, -1});  // left
        directions.add(new int[]{-1, 0});  // up
        directions.add(new int[]{-1, -1}); // up-left
        directions.add(new int[]{1, -1});  // down-left
        
        Collections.shuffle(directions, random);
        for (int attempt = 0; attempt < 100; attempt++) {
            int dirIdx = random.nextInt(directions.size());
            int[] dir = directions.get(dirIdx);
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            if (canPlaceWord(word, row, col, dir[0], dir[1])) {
                for (int k = 0; k < word.length(); k++) {
                    grid[row + k * dir[0]][col + k * dir[1]] = word.charAt(k);
                }
                return;
            }
        }
        // If not placed after 100 tries, skip
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

    public void printGrid() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        logger.info("Welcome to the Puzzle Generator!");
        List<String> words = readWordsFromFile(PuzzleProperties.getProperty("word-search.file.path"));
        WordSearchPuzzleGenerator generator = new WordSearchPuzzleGenerator(words);
        generator.generate();
        generator.printGrid();
        logger.info("Puzzle grid printed to console.");
    }
}
