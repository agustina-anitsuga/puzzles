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
            buildPuzzleForPhrase(phrase);
        } else {
            logger.warn("No phrases found.");
        }
    }

    private void buildPuzzleForPhrase(Phrase phrase) {
        logger.info("Random Phrase: " + phrase);

        Puzzle puzzle = buildPuzzle(phrase);

        PuzzleFileWriter puzzleFileWriter = new PuzzleFileWriter(puzzle);
        puzzleFileWriter.generateClueFile(getProperty("puzzles.output.dir"), puzzle.getName()+"-clue.txt");
        puzzleFileWriter.generateSolutionFile(getProperty("puzzles.output.dir"), puzzle.getName()+"-sol.txt");

        PuzzleImageWriter puzzleImageWriter = new PuzzleImageWriter(puzzle);
        puzzleImageWriter.generate(getProperty("puzzles.output.dir"), puzzle.getName()+"-sol.png", true);
        puzzleImageWriter.generate(getProperty("puzzles.output.dir"), puzzle.getName()+".png", false);
    }

    public Puzzle buildPuzzle(Phrase phrase) {
        List<Word> selectedWords = getSelectedWords(phrase);
        Puzzle puzzle = new Puzzle(LocalDateTime.now(), phrase, selectedWords);
        logger.info(puzzle.toString());
        return puzzle;
    }

    private List<Word> getSelectedWords(Phrase phrase) {
        WordReader wordReader = new WordReader(getProperty("word.list.file.path"));

        List<Word> selectedWords = new ArrayList<>();

        if(phrase.chunkCount()>1) { // only up to 2 phrases supported currently
            int i=0;
            String secondPhrase = phrase.getChunks().getLast();
            for (char c1 : phrase.getChunks().getFirst().toCharArray()) {
                if( secondPhrase.length() > i ) {
                char c2 = secondPhrase.charAt(i++);
                Word word = wordReader.getWordWith(c1, c2,3);
                    if (word != null) {
                        selectedWords.add(word);
                    } else {
                        selectedWords.add(new Word());
                        logger.warn("No word found for characters: " + c1 + ", " + c2);
                    }
                } else {
                    Word word = wordReader.getWordWith(c1);
                    if (word != null) {
                        selectedWords.add(word);
                    } else {
                        selectedWords.add(new Word());
                        logger.warn("No word found for character: " + c1);
                    }
                }
            }
        } else {
            for (char c : phrase.getChunks().getFirst().toCharArray()) {
                Word word = wordReader.getWordWith(c);
                if (word != null) {
                    selectedWords.add(word);
                } else {
                    selectedWords.add(new Word());
                    logger.warn("No word found for character: " + c);
                }
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
