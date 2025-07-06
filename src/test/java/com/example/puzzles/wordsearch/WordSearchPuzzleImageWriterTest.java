package com.example.puzzles.wordsearch;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.puzzles.model.Word;
import com.example.puzzles.model.WordSearchPosition;
import com.example.puzzles.model.WordSearchPuzzle;
import com.example.puzzles.model.Coordinate;
import com.example.puzzles.model.Direction;
import static org.junit.jupiter.api.Assertions.*;

public class WordSearchPuzzleImageWriterTest {
    @Test
    public void testWriteToFileCreatesImage() throws Exception {
        char[][] grid = {
            {'A','B','C','D','E'},
            {'F','G','H','I','J'},
            {'K','L','M','N','O'},
            {'P','Q','R','S','T'},
            {'U','V','W','X','Y'}
        };
        List<Word> words = new ArrayList<>();
        words.add(new Word(1, "ABC", new WordSearchPosition(new Coordinate(0,0), Direction.RIGHT)));
        words.add(new Word(2, "MNO", new WordSearchPosition(new Coordinate(2,2), Direction.RIGHT)));
        words.add(new Word(3, "STU", new WordSearchPosition(new Coordinate(3,3), Direction.RIGHT)));
        WordSearchPuzzle puzzle = new WordSearchPuzzle(LocalDateTime.now(), grid, words);
        WordSearchPuzzleImageWriter writer = new WordSearchPuzzleImageWriter(puzzle);
        String filePath = "test-wordsearch.png";
        writer.writeToFile(filePath,true);
        java.io.File file = new java.io.File(filePath);
        assertTrue(file.exists() && file.length() > 0, "Image file should be created and not empty");
        assertTrue(file.delete(), "Image file should be deleted after test");
    }
}
