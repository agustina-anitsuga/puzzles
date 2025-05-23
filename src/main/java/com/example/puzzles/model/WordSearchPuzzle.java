package com.example.puzzles.model;

import java.time.LocalDateTime;
import java.util.List;


public class WordSearchPuzzle extends Puzzle {

    private final char[][] grid;

    public WordSearchPuzzle(LocalDateTime generation, char[][] grid, List<Word> words) {
        super(generation, words);
        this.grid = grid;
    }

    public char[][] getGrid() {
        return grid;
    }

    public String getGridAsString() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            for (char cell : row) {
                sb.append(cell).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    protected String getNameStem() {
        return "word-search-puzzle-";
    }

}
