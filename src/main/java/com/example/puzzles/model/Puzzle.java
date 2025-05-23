package com.example.puzzles.model;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Puzzle {
    
    private LocalDateTime generation;
    private List<Word> words;

    public Puzzle(LocalDateTime generation, List<Word> words) {
        this.words = words;
        this.generation = generation;
    }

    public List<Word> getWords() {
        return words;
    }

    public LocalDateTime getGeneration() {
        return generation;
    }

    public String getName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return getNameStem() + generation.format(formatter);
    }

    protected String getNameStem() {
        return "puzzle-";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Words:\n");
        for (Word word : words) {
            sb.append(word).append("\n");
        }
        return sb.toString();
    }

}
