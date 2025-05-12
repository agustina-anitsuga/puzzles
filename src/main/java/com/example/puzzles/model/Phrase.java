package com.example.puzzles.model;

public class Phrase {
    
    private int id;
    private String phrase;
    private String book;
    private String author;

    public Phrase(int id, String phrase, String book, String author) {
        this.id = id;
        this.phrase = phrase;
        this.book = book;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getCharactersInPhrase() {
        return phrase.replaceAll("[\\p{Punct}\\s]", "").toLowerCase();
    }

    public String getBook() {
        return book;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "Phrase{" +
                "id=" + id +
                ", phrase='" + phrase + '\'' +
                ", book='" + book + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
