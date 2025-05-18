package com.example.puzzles.acrostics;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.PhraseReader;
import com.example.puzzles.tools.WordReader;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.tools.PuzzleProperties;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AcrosticPuzzleGenerator {

    private static final Logger logger = LogManager.getLogger(AcrosticPuzzleGenerator.class);

    public static void main(String[] args) {
        logger.info("Welcome to the Puzzle Generator!");
        AcrosticPuzzleGenerator generator = new AcrosticPuzzleGenerator();
        generator.generate();
        logger.info("Puzzle generation completed.");
    }

    public void generate() {
        String filePath = PuzzleProperties.getProperty("phrases.file.path");
        Phrase phrase = new PhraseReader().getRandomPhrase(new File(filePath).getAbsolutePath());
        if (phrase != null) {
            buildPuzzleForPhrase(phrase);
        } else {
            logger.warn("No phrases found.");
        }
    }

    private void buildPuzzleForPhrase(Phrase phrase) {
        logger.info("Random Phrase: " + phrase);

        Puzzle puzzle = buildPuzzle(phrase);

        AcrosticPuzzleFileWriter puzzleFileWriter = new AcrosticPuzzleFileWriter(puzzle);
        puzzleFileWriter.generateClueFile(PuzzleProperties.getProperty("puzzles.output.dir"), puzzle.getName()+"-clue.txt");
        puzzleFileWriter.generateSolutionFile(PuzzleProperties.getProperty("puzzles.output.dir"), puzzle.getName()+"-sol.txt");

        AcrosticPuzzleImageWriter puzzleImageWriter = new AcrosticPuzzleImageWriter(puzzle);
        puzzleImageWriter.generate(PuzzleProperties.getProperty("puzzles.output.dir"), puzzle.getName()+"-sol.png", true);
        puzzleImageWriter.generate(PuzzleProperties.getProperty("puzzles.output.dir"), puzzle.getName()+".png", false);
    }

    public Puzzle buildPuzzle(Phrase phrase) {
        List<Word> selectedWords = getSelectedWords(phrase);
        Puzzle puzzle = new Puzzle(LocalDateTime.now(), phrase, selectedWords);
        logger.info(puzzle.toString());
        return puzzle;
    }

    private List<Word> getSelectedWords(Phrase phrase) {
        WordReader wordReader = new WordReader(PuzzleProperties.getProperty("word.list.file.path"));

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

}
