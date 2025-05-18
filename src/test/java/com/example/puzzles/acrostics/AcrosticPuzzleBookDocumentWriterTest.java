package com.example.puzzles.acrostics;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AcrosticPuzzleBookDocumentWriterTest {
    @Test
    public void testCreateDocumentNotNull() throws Exception {
        List<Word> words = List.of(
            new Word(1, "apple", "A fruit"),
            new Word(2, "banana", "Another fruit"),
            new Word(3, "cherry", "Yet another fruit")
        );
        Phrase phrase = new Phrase(1, "abc", "Book", "Author");
        Puzzle puzzle = new Puzzle(java.time.LocalDateTime.now(), phrase, words);
        List<Puzzle> puzzles = List.of(puzzle);
        File file = File.createTempFile("output/puzzle-1", ".png");
        List<String> imagePaths = List.of(file.getAbsolutePath());
        AcrosticPuzzleBookDocumentWriter writer = new AcrosticPuzzleBookDocumentWriter();
        XWPFDocument doc = writer.createDocument(puzzles, imagePaths);
        assertNotNull(doc, "Document should not be null");
        file.delete();
    }
}
