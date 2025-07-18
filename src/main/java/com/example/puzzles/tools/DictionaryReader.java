package com.example.puzzles.tools;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.example.puzzles.model.Word;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DictionaryReader {

    private List<Word> words;

    public DictionaryReader ( ){
    }

    public DictionaryReader (List<Word> words) {
        this.words = words;
    }

    public DictionaryReader ( String filePath ){
        try {
            words = this.readWordsFromExcel(filePath);
        } catch (IOException e) {
            System.err.println("Error reading words from Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Word> readWordsFromExcel(String filePath) throws IOException {
        List<Word> twords = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Skip header row
                    continue;
                }

                int id = (int) row.getCell(0).getNumericCellValue();
                String word = row.getCell(1).getStringCellValue();
                String definition = row.getCell(2).getStringCellValue();

                twords.add(new Word(id, word, definition));
            }
        }

        return twords;
    }


    public Word getWordWithIn(char character, int position) {
        List<Word> matchingWords = new ArrayList<>();
        
        for (Word word : words) {
            if ( word.charAt(position) == character) {
                matchingWords.add(word);
            }
        }

        if (matchingWords.isEmpty()) {
            return null; // Return null if no word contains the character
        }

        Random random = new Random();
        return matchingWords.get(random.nextInt(matchingWords.size()));
    }

    public Word getWordWith(char character) {
        List<Word> matchingWords = new ArrayList<>();
        
        for (Word word : words) {
            if ( (word.indexOf(character)) != -1) {
                matchingWords.add(word);
            }
        }

        if (matchingWords.isEmpty()) {
            return null; // Return null if no word contains the character
        }

        Random random = new Random();
        return matchingWords.get(random.nextInt(matchingWords.size()));
    }

    public Word getWordWith(char a, char b, int distance) {
        List<Word> matchingWords = new ArrayList<>();
        
        for (Word word : words) {
            int index = word.indexOf(a);
            if (index == -1) {
                continue; 
            } else {
                int newIndex = index + distance;
                if( word.getWord().length()>newIndex && word.charAt(newIndex)==b){
                    matchingWords.add(word);
                }
            }
        }

        if (matchingWords.isEmpty()) {
            return null; // Return null if no word contains the characters
        }

        Random random = new Random();
        return matchingWords.get(random.nextInt(matchingWords.size()));
    }
}
