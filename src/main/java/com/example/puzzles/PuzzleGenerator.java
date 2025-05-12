package com.example.puzzles;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Word;
import com.example.puzzles.model.Puzzle;

import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PuzzleGenerator {

    private static final Logger logger = LogManager.getLogger(PuzzleGenerator.class);

    public static void main(String[] args) {
        logger.info("Welcome to the Puzzle Generator!");
        PuzzleGenerator generator = new PuzzleGenerator();
        generator.generate();
        logger.info("Puzzle generation completed.");
    }

    public void generate() {

        PhraseReader phraseReader = new PhraseReader();
        Phrase phrase = phraseReader.getRandomPhrase("phrases/Iconic_Book_Quotes.xlsx");

        if (phrase != null) {

            logger.info("Random Phrase: " + phrase.getPhrase());
            logger.info("Book: " + phrase.getBook());
            logger.info("Author: " + phrase.getAuthor());

            List<Word> selectedWords = getSelectedWords(phrase);

            Puzzle puzzle = new Puzzle(phrase, selectedWords);
            logger.info(puzzle.toString());

            PuzzleImage puzzleImage = new PuzzleImage(puzzle);
            puzzleImage.generate("src/main/resources/images", "puzzle.png");

        } else {
            logger.warn("No phrases found.");
        }
    }

    private List<Word> getSelectedWords(Phrase phrase) {
        WordReader wordReader = new WordReader("src/main/resources/words/Word_List.xlsx");

        List<Word> selectedWords = new ArrayList<>();
        for (char c : phrase.getCharactersInPhrase().toCharArray()) {
            Word word = wordReader.getWordWith(c);
            if (word != null) {
                selectedWords.add(word);
            } else {
                selectedWords.add(new Word());
                logger.warn("No word found for character: " + c);
            }
        }
        return selectedWords;
    }

}
