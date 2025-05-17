package com.example.puzzles.tools;

import com.example.puzzles.model.Word;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordReaderTest {

    @BeforeEach
    public void setup() {
        String filePath = "src/test/resources/words/Test_Word_List.xlsx";
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("id");
            header.createCell(1).setCellValue("word");
            header.createCell(2).setCellValue("definition");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue(1);
            row1.createCell(1).setCellValue("apple");
            row1.createCell(2).setCellValue("A fruit");

            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue(2);
            row2.createCell(1).setCellValue("banana");
            row2.createCell(2).setCellValue("Another fruit");

            Row row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue(3);
            row3.createCell(1).setCellValue("cherry");
            row3.createCell(2).setCellValue("Yet another fruit");

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        } catch (IOException e) {
            fail("Failed to create a valid Test_Word_List.xlsx file: " + e.getMessage());
        }
    }

    @Test
    public void testReadWordsFromExcel() {
        WordReader wordReader = new WordReader();
        URL resource = getClass().getClassLoader().getResource("words/Test_Word_List.xlsx");
        assertNotNull(resource, "Test file not found: Test_Word_List.xlsx");

        try {
            List<Word> words = wordReader.readWordsFromExcel(resource.getPath());
            assertNotNull(words, "Words list should not be null");
            assertFalse(words.isEmpty(), "Words list should not be empty");
            assertEquals(3, words.size(), "Words list should contain 3 entries");
        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }

    @Test
    public void testGetWordWith() {
        
        List<Word> words = List.of(
            new Word(1, "apple", "A fruit"),
            new Word(2, "banana", "Another fruit"),
            new Word(3, "cherry", "Yet another fruit")
        );
        WordReader wordReader = new WordReader(words);

        Word word = wordReader.getWordWith('a');
        assertNotNull(word, "A word containing the character 'a' should be found");
        assertTrue(word.getWord().contains("a"), "The word should contain the character 'a'");

        Word nonExistent = wordReader.getWordWith('z');
        assertNull(nonExistent, "No word should be found containing the character 'z'");
    }

    @Test
    public void testGetWordWithMultipleMatches() {
        List<Word> words = List.of(
            new Word(1, "apple", "A fruit"),
            new Word(2, "banana", "Another fruit"),
            new Word(3, "cherry", "Yet another fruit")
        );
        WordReader wordReader = new WordReader(words);

        Word word = wordReader.getWordWith('a');
        assertNotNull(word, "A word containing the character 'a' should be found");
        assertTrue(word.getWord().contains("a"), "The word should contain the character 'a'");
    }

    @Test
    public void verifyTestWordListFile() {
        String filePath = "src/test/resources/words/Test_Word_List.xlsx";
        File file = new File(filePath);
        assertTrue(file.exists(), "The Test_Word_List.xlsx file should exist");
        assertTrue(file.length() > 0, "The Test_Word_List.xlsx file should not be empty");
    }
}
