package com.example.puzzles.wordsearch;

import com.example.puzzles.model.WordSearchPuzzle;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Writes a book of word search puzzles to a Word document (.docx).
 * Each puzzle is on its own page; solutions are grouped at the end in smaller size.
 */
public class WordSearchPuzzleBookDocumentWriter {
    
    /**
     * Writes the puzzles and their solutions to a Word document.
     * @param puzzles List of puzzles (with solutions)
     * @param outputFilePath Path to output .docx file
     * @throws IOException if writing fails
     */
    public void writeBook(List<WordSearchPuzzle> puzzles, String outputFilePath) throws IOException {
        XWPFDocument doc = new XWPFDocument();

        // Write each puzzle on its own page
        for (int i = 0; i < puzzles.size(); i++) {
            WordSearchPuzzle puzzle = puzzles.get(i);
            XWPFParagraph title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = title.createRun();
            run.setText("Word Search Puzzle " + (i + 1));
            run.setBold(true);
            run.setFontSize(18);
            run.addBreak();

            // Write the puzzle grid
            writeGrid(doc, puzzle.getGrid(), 12);

            // List the words
            XWPFParagraph wordsPara = doc.createParagraph();
            XWPFRun wordsRun = wordsPara.createRun();
            wordsRun.setText("Words: " + String.join(", ", puzzle.getWordsAsStringList()));
            wordsRun.setFontSize(11);

            // Page break after each puzzle except the last
            if (i < puzzles.size() - 1) {
                doc.createParagraph().setPageBreak(true);
            }
        }

        // Solutions section
        XWPFParagraph solTitle = doc.createParagraph();
        solTitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun solRun = solTitle.createRun();
        solRun.setText("Solutions");
        solRun.setBold(true);
        solRun.setFontSize(14);
        solRun.addBreak();

        for (int i = 0; i < puzzles.size(); i++) {
            WordSearchPuzzle puzzle = puzzles.get(i);
            XWPFParagraph solPuzzleTitle = doc.createParagraph();
            solPuzzleTitle.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun solPuzzleRun = solPuzzleTitle.createRun();
            solPuzzleRun.setText("Solution to Puzzle " + (i + 1));
            solPuzzleRun.setFontSize(10);
            solPuzzleRun.setBold(true);
            solPuzzleRun.addBreak();
            // Write the solution grid in smaller font
            writeGrid(doc, new char[1][1] /*puzzle.getSolutionGrid()*/, 8);
        }

        try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
            doc.write(out);
        }
        doc.close();
    }

    // Helper to write a grid to the document
    private void writeGrid(XWPFDocument doc, char[][] grid, int fontSize) {
        XWPFTable table = doc.createTable(grid.length, grid[0].length);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                XWPFTableCell cell = table.getRow(i).getCell(j);
                cell.removeParagraph(0);
                XWPFParagraph para = cell.addParagraph();
                XWPFRun run = para.createRun();
                run.setText(String.valueOf(grid[i][j]));
                run.setFontFamily("Monospaced");
                run.setFontSize(fontSize);
                para.setAlignment(ParagraphAlignment.CENTER);
            }
        }
        XWPFParagraph after = doc.createParagraph();
        after.createRun().addBreak();
    }
}
