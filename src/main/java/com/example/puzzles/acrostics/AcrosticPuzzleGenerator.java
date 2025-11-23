package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.AcrosticPuzzlePosition;
import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.PhraseReader;
import com.example.puzzles.tools.DictionaryReader;
import com.example.puzzles.tools.PuzzleProperties;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AcrosticPuzzleGenerator {

    private static final Logger logger = LogManager.getLogger(AcrosticPuzzleGenerator.class);

    private static final int DISTANCE = 3;

    public static void main(String[] args) throws Exception {
        logger.info("Welcome to the Puzzle Generator!");
        AcrosticPuzzleGenerator generator = new AcrosticPuzzleGenerator();
        generator.generate();
        logger.info("Puzzle generation completed.");
    }

    public void generate() throws Exception {
        String filePath = PuzzleProperties.getProperty("phrases.file.path");
        Phrase phrase = new PhraseReader().getRandomPhrase(new File(filePath).getAbsolutePath());
        if (phrase != null) {
            buildPuzzleForPhrase(phrase);
        } else {
            logger.warn("No phrases found.");
        }
    }

    private AcrosticPuzzle buildPuzzleForPhrase(Phrase phrase) throws Exception {
        logger.info("Phrase: " + phrase);
        phrase.setDistanceBetweenChunks(DISTANCE);       
        AcrosticPuzzle puzzle = buildPuzzle(phrase);
        generatePuzzleFiles(puzzle);
        return puzzle;
    }

    public void generatePuzzleFiles(AcrosticPuzzle puzzle) throws Exception {
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");

        AcrosticPuzzleFileWriter puzzleFileWriter = new AcrosticPuzzleFileWriter(puzzle);
        puzzleFileWriter.generatePuzzleFile(outputDir, puzzle.getName() );
        puzzleFileWriter.generateClueFile(outputDir, puzzle.getName()+"-clue.txt");
        puzzleFileWriter.generateSolutionFile(outputDir, puzzle.getName()+"-sol.txt");

        AcrosticPuzzleImageWriter puzzleImageWriter = new AcrosticPuzzleImageWriter(puzzle);
        puzzleImageWriter.generate(outputDir, puzzle.getName()+"-sol.png", true);
        puzzleImageWriter.generate(outputDir, puzzle.getName()+".png", false);

        AcrosticCharacterCluesImageWriter characterClueImageWriter = new AcrosticCharacterCluesImageWriter(puzzle);
        characterClueImageWriter.generate(outputDir, puzzle.getName()+"-character-clue.png");
    }

    public AcrosticPuzzle buildPuzzle(Phrase phrase) {
        phrase.setDistanceBetweenChunks(DISTANCE);
        List<Word> selectedWords = selectWords(phrase);
        AcrosticPuzzle puzzle = new AcrosticPuzzle(LocalDateTime.now(), phrase, selectedWords);
        logger.info(puzzle.toString());
        return puzzle;
    }

    private List<Word> selectWords(Phrase phrase) {
        DictionaryReader wordReader = new DictionaryReader(PuzzleProperties.getProperty("word.list.file.path"));

        List<Word> selectedWords = new ArrayList<>();

        if(phrase.chunkCount()>1) { // only up to 2 phrases supported currently
            int i=0;
            String secondPhrase = phrase.getChunks().getLast();
            for (char c1 : phrase.getChunks().getFirst().toCharArray()) {
                if( secondPhrase.length() > i ) {
                    char c2 = secondPhrase.charAt(i++);
                    Word word = wordReader.getWordWith(c1, c2,DISTANCE);
                    if (word != null) {
                        int indexC1 = this.getIntersectingIndex(c1, c2, word.getWord());
                        AcrosticPuzzlePosition position = new AcrosticPuzzlePosition(
                            List.of(indexC1,indexC1+DISTANCE), 
                            List.of(0,1)
                        );
                        word.setPosition(position);
                        selectedWords.add(word);
                    } else {
                        logger.warn("No word found for characters: " + c1 + ", " + c2);
                        word = wordReader.getWordWithIn(c2,1);
                        if (word != null) {
                            AcrosticPuzzlePosition position = new AcrosticPuzzlePosition(
                                    List.of(word.getWord().indexOf(c2)), 
                                    List.of(1)
                            );
                            word.setPosition(position);
                            selectedWords.add(word);
                        } else {
                            selectedWords.add(new Word());
                            logger.warn("No word found for character: " + c2);
                        }
                    }
                } else {
                    Word word = wordReader.getWordWith(c1);
                    if (word != null) {
                        AcrosticPuzzlePosition position = new AcrosticPuzzlePosition(word.getWord().indexOf(c1));
                        word.setPosition(position);
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
                    AcrosticPuzzlePosition position = new AcrosticPuzzlePosition(word.getWord().indexOf(c));
                    word.setPosition(position);
                    selectedWords.add(word);
                } else {
                    selectedWords.add(new Word());
                    logger.warn("No word found for character: " + c);
                }
            }
        }
        return selectedWords;
    }

    private int getIntersectingIndex(char charC1, char charC2, String word) {
        int indexC1 = word.indexOf(charC1);
        if( indexC1 >=0 ){
            if( (word.length() > (indexC1+DISTANCE)) && word.charAt(indexC1 + DISTANCE) == charC2 ) {
                return indexC1; // Found both characters in the word
            } else {
                indexC1 = word.indexOf(charC1,indexC1+1); 
                if( indexC1 < 0 ){
                    logger.warn("Characters not found in word: " + word);
                    return -1; // Character not found
                } else {
                    if( (word.length() > indexC1 + DISTANCE) && (word.charAt(indexC1 + DISTANCE) == charC2) ) {
                        return indexC1; // Found both characters in the word
                    } else {
                        logger.warn("Characters not found in word: " + word);
                        return -1; // Character not found
                    }
                }
            }
        }
        return indexC1;
    }
}
