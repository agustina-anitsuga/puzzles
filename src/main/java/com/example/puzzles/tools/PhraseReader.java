package com.example.puzzles.tools;

import com.example.puzzles.model.Phrase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PhraseReader {

    private static final Logger logger = LogManager.getLogger(PhraseReader.class);

    public List<Phrase> readPhrasesFromExcel(String filePath) throws IOException {
        List<Phrase> phrases = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            if (workbook.getNumberOfSheets() == 0) {
                return phrases; // Return empty list if no sheets are present
            }

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0) {
                return phrases; // Return empty list if the sheet has no rows
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Skip header row
                    continue;
                }

                if (row.getPhysicalNumberOfCells() < 4) {
                    continue; // Skip rows with insufficient cells
                }

                int id = (int) row.getCell(0).getNumericCellValue();
                String phrase = row.getCell(1).getStringCellValue();
                String book = row.getCell(2).getStringCellValue();
                String author = row.getCell(3).getStringCellValue();

                phrases.add(new Phrase(id, phrase, book, author));
            }
        }

        return phrases;
    }

    public Phrase getRandomPhrase(String filePath) {

        // read the file
        List<Phrase> phrases = null;
        try {
            phrases = readPhrasesFromExcel(new File(filePath).getPath());
        } catch (IOException e) {
            logger.error("Error reading phrases from Excel file: " + e.getMessage(), e);
            return null;
        }

        // select random phrase
        Random random = new Random();
        int randomIndex = random.nextInt(phrases.size());
        return phrases.get(randomIndex);
    }
}
