package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.PhraseReader;
import com.example.puzzles.tools.WordReader;
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

    private AcrosticPuzzle buildPuzzleForPhrase(Phrase phrase) {
        logger.info("Random Phrase: " + phrase);

        AcrosticPuzzle puzzle = buildPuzzle(phrase);
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");

        AcrosticPuzzleFileWriter puzzleFileWriter = new AcrosticPuzzleFileWriter(puzzle);
        puzzleFileWriter.generateClueFile(outputDir, puzzle.getName()+"-clue.txt");
        puzzleFileWriter.generateSolutionFile(outputDir, puzzle.getName()+"-sol.txt");

        AcrosticPuzzleImageWriter puzzleImageWriter = new AcrosticPuzzleImageWriter(puzzle);
        puzzleImageWriter.generate(outputDir, puzzle.getName()+"-sol.png", true);
        puzzleImageWriter.generate(outputDir, puzzle.getName()+".png", false);

        AcrosticCharacterCluesImageWriter characterClueImageWriter = new AcrosticCharacterCluesImageWriter(puzzle);
        characterClueImageWriter.generate(outputDir, puzzle.getName()+"-character-clue.png");

        return puzzle;
    }

    public AcrosticPuzzle buildPuzzle(Phrase phrase) {
        List<Word> selectedWords = selectWords(phrase);
        AcrosticPuzzle puzzle = new AcrosticPuzzle(LocalDateTime.now(), phrase, selectedWords);
        logger.info(puzzle.toString());
        return puzzle;
    }

    private List<Word> selectWords(Phrase phrase) {
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
