package com.example.puzzles.model;

public class Word {
    
    private int id;
    private String word;
    private String definition;

    public Word() {
        this.id = 0;
        this.word = "";
        this.definition = "";
    }

    public Word(int id, String word, String definition) {
        this.id = id;
        this.word = word;
        this.definition = definition;
    }

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", definition='" + definition + '\'' +
                '}';
    }

    public int indexOf(char character) {
        return word.toLowerCase().indexOf(character);
    }

    public char charAt(int index) {
        return word.toLowerCase().charAt(index);
    }
}
