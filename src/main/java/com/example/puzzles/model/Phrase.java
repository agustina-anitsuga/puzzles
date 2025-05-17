package com.example.puzzles.model;

import java.util.List;

public class Phrase {
    
    private int id;
    private String phrase;
    private List<String> chunks;
    private String book;
    private String author;
    private int distanceBetweenChunks = 2;

    public Phrase(int id, String phrase, String book, String author) {
        this.id = id;
        this.phrase = phrase;
        this.chunks = chunks(phrase);
        this.book = book;
        this.author = author;
    }

    public int getDistanceBetweenChunks() {
        return distanceBetweenChunks;
    }

    public void setDistanceBetweenChunks(int distanceBetweenChunks) {
        this.distanceBetweenChunks = distanceBetweenChunks;
    }

    private List<String> chunks(String aPhrase) {
        String charactersInPhrase = getCharactersIn(aPhrase);
        List<String> chunks = List.of(charactersInPhrase);
        if( charactersInPhrase.length() > 30) {
            int divisor = (int) Math.ceil((charactersInPhrase.length()+1)/2);
            chunks = List.of(
                    charactersInPhrase.substring(0, divisor),
                    charactersInPhrase.substring(divisor)
            );
        }
        return chunks;
    }

    public int getId() {
        return id;
    }

    public String getCharactersInPhrase() {
        return getCharactersIn(phrase);
    }

    private String getCharactersIn(String chunk) {
        return chunk.replaceAll("[\\p{Punct}\\s]", "")
                     .replaceAll("'","")
                     .replaceAll("’", "")
                     .replaceAll("\\?", "")
                     .replaceAll("”", "")
                     .replaceAll("\\.", "")
                     .replaceAll("…", "")
                     .toLowerCase();
    }

    public String getBook() {
        return book;
    }

    public String getAuthor() {
        return author;
    }

    public int chunkCount(){
        return chunks.size();
    }

    public List<String> getChunks(){
        return chunks;
    }

    public String getPhrase() {
        return phrase;
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

    public int length() {
        return phrase.length();
    }

    public char[] toCharArray() {
        return phrase.toCharArray();
    }

    public char charAt(int index) {
        return phrase.charAt(index);
    }

}
