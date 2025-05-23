package com.example.puzzles.model;

import java.time.LocalDateTime;
import java.util.List;

public class AcrosticPuzzle extends Puzzle {
    
    private Phrase phrase;

    public AcrosticPuzzle(LocalDateTime generation, Phrase phrase, List<Word> words) {
        super(generation, words);
        this.phrase = phrase;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    public int getPhraseLength() {
        return phrase.length();
    }
        
    protected String getNameStem() {
        return "acrostic-puzzle-";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(phrase).append("\n");
        sb.append("Words:\n");
        for (Word word : getWords()) {
            sb.append(word).append("\n");
        }
        return sb.toString();
    }

}
