package com.example.puzzles.model;

import java.util.List;

public class Puzzle {
    
    private Phrase phrase;
    private List<Word> words;

    public Puzzle(Phrase phrase, List<Word> words) {
        this.phrase = phrase;
        this.words = words;
    }

    public Phrase getPhrase() {
        return phrase;
    }

    public int getPhraseLength() {
        return phrase.getCharactersInPhrase().length();
    }

    public List<Word> getWords() {
        return words;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Phrase: ").append(phrase).append("\n");
        sb.append("Words:\n");
        for (Word word : words) {
            sb.append(word).append("\n");
        }
        return sb.toString();
    }
}
