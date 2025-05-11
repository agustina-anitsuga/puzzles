package com.example.puzzles;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Word;
import com.example.puzzles.model.Puzzle;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class PuzzleGenerator {
    
    public static void main(String[] args) {
        System.out.println("Welcome to the Puzzle Generator!");
        PuzzleGenerator generator = new PuzzleGenerator();
        generator.generate();
        System.out.println("Puzzle generation completed.");
    }

    public void generate() {
    
        PhraseReader phraseReader = new PhraseReader();
        Phrase phrase = phraseReader.getRandomPhrase("phrases/Iconic_Book_Quotes.xlsx");
        
        if (phrase != null) {

            System.out.println("Random Phrase: " + phrase.getPhrase());
            System.out.println("Book: " + phrase.getBook());
            System.out.println("Author: " + phrase.getAuthor());

            List<Word> selectedWords = getSelectedWords(phrase);

            Puzzle puzzle = new Puzzle(phrase.getPhrase(), selectedWords);
            System.out.println(puzzle);

            PuzzleImage puzzleImage = new PuzzleImage(puzzle);
            puzzleImage.generate("puzzles/src/main/resources/images", "puzzle.png");
            
        } else {
            System.out.println("No phrases found.");
        }
    }

    private List<Word> getSelectedWords(Phrase phrase) {
        WordReader wordReader = new WordReader();
        List<Word> words = loadWords(wordReader);

        List<Word> selectedWords = new ArrayList<>();
        for (char c : phrase.getPhrase().toCharArray()) {
            Word word = wordReader.getWordWith(c, words);
            if (word != null) {
                selectedWords.add(word);
            }
        }
        return selectedWords;
    }

    private List<Word> loadWords(WordReader wordReader) {
        try {
            return wordReader.readWordsFromExcel("src/main/resources/words/Word_List.xlsx");
        } catch (IOException e) {
            System.err.println("Error reading words from Excel file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}
