package com.example.puzzles.model;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Puzzle {
    
    private LocalDateTime generation;
    private Phrase phrase;
    private List<Word> words;

    public Puzzle(LocalDateTime generation, Phrase phrase, List<Word> words) {
        this.phrase = phrase;
        this.words = words;
        this.generation = generation;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    public int getPhraseLength() {
        return phrase.length();
    }

    public List<Word> getWords() {
        return words;
    }

    public String getName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return "acrostic-puzzle-" + generation.format(formatter);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(phrase).append("\n");
        sb.append("Words:\n");
        for (Word word : words) {
            sb.append(word).append("\n");
        }
        return sb.toString();
    }

}
