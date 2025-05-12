package com.example.puzzles;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Word;
import com.example.puzzles.model.Puzzle;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.io.IOException;

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
        Phrase phrase = phraseReader.getRandomPhrase(getProperty("phrases.file.path"));

        if (phrase != null) {

            logger.info("Random Phrase: " + phrase.getPhrase());
            logger.info("Book: " + phrase.getBook());
            logger.info("Author: " + phrase.getAuthor());

            List<Word> selectedWords = getSelectedWords(phrase);

            Puzzle puzzle = new Puzzle(LocalDateTime.now(), phrase, selectedWords);
            logger.info(puzzle.toString());

            PuzzleFileGenerator puzzleFileGenerator = new PuzzleFileGenerator(puzzle);
            puzzleFileGenerator.generateClueFile(getProperty("puzzles.output.dir"), puzzle.getName()+"-clue.txt");
            puzzleFileGenerator.generateSolutionFile(getProperty("puzzles.output.dir"), puzzle.getName()+"-sol.txt");

            PuzzleImage puzzleImage = new PuzzleImage(puzzle);
            puzzleImage.generate(getProperty("puzzles.output.dir"), puzzle.getName()+"-sol.png", true);
            puzzleImage.generate(getProperty("puzzles.output.dir"), puzzle.getName()+".png", false);
            
        } else {
            logger.warn("No phrases found.");
        }
    }

    private List<Word> getSelectedWords(Phrase phrase) {
        WordReader wordReader = new WordReader(getProperty("word.list.file.path"));

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

    private String getProperty(String key) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties properties = new Properties();
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            properties.load(input);
            return properties.getProperty(key);
        } catch (IOException e) {
            logger.error("Error reading property: " + key, e);
            return null;
        }
    }

}
