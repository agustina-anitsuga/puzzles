package com.example.puzzles.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AcrosticPuzzle extends Puzzle {
    
    private Phrase phrase;

    public AcrosticPuzzle(){}

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

    public List<Character> getSortedCharacters() {
        List<Character> chars = new ArrayList<>();
        this.getWords().stream()
            .flatMap(word -> word.getWord().chars().mapToObj(c -> (char) c))
            .sorted()
            .forEach(chars::add);
        return chars;
    }
}
