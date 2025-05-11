package com.example.puzzles.wiktionary;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

public class DictionaryBuilder {

    public static void buildDictionary(String inputFilePath, String outputFilePath) throws IOException {
        WiktionaryDefinitionFetcher fetcher = new WiktionaryDefinitionFetcher();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {

            Sheet sheet = workbook.createSheet("Word List");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Word");
            headerRow.createCell(2).setCellValue("Definition");

            String line;
            int id = 1;
            int rowIndex = 1;

            while ((line = reader.readLine()) != null) {
                System.out.println("Processing word: " + line);
                Thread.sleep(1000); // Add a 1-second delay between service calls
                List<String> definitions = fetcher.fetchDefinition(line);
                if (definitions.isEmpty()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(id++);
                    row.createCell(1).setCellValue(line);
                    row.createCell(2).setCellValue("No definition found.");
                } else {
                    for (String definition : definitions) {
                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue(id);
                        row.createCell(1).setCellValue(line);
                        row.createCell(2).setCellValue(definition);
                    }
                    id++;
                }
            }

            workbook.write(fileOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            String inputFilePath = "src/main/resources/words/wiktionary-words.txt";
            String outputFilePath = "src/main/resources/words/Word_List.xlsx";
            buildDictionary(inputFilePath, outputFilePath);
            System.out.println("Dictionary built successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
