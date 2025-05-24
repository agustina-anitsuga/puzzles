package com.example.puzzles.wordsearch;

import com.example.puzzles.model.WordSearchPuzzle;
import com.example.puzzles.tools.PuzzleProperties;
import com.example.puzzles.tools.BookDocumentWriter;

import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Writes a book of word search puzzles to a Word document (.docx).
 * Each puzzle is on its own page; solutions are grouped at the end in smaller size.
 */
public class WordSearchPuzzleBookDocumentWriter extends BookDocumentWriter {
    
    /**
     * Writes the puzzles and their solutions to a Word document.
     * @param puzzles List of puzzles (with solutions)
     * @param outputFilePath Path to output .docx file
     * @throws IOException if writing fails
     */
    public void writeBook(List<WordSearchPuzzle> puzzles, String outputFilePath) throws Exception {
        XWPFDocument doc = super.createDocument();
        writePuzzlesSection(doc, puzzles);
        writeSolutionsSection(doc, puzzles);
        super.endDocument(doc);
        try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
            doc.write(out);
        }
        doc.close();
    }

    private void writePuzzlesSection(XWPFDocument doc, List<WordSearchPuzzle> puzzles) {
        // Write each puzzle on its own page
        for (int i = 0; i < puzzles.size(); i++) {
            WordSearchPuzzle puzzle = puzzles.get(i);
            writePuzzleTitle(doc, i);

            // Write the puzzle grid
            writePuzzle(doc, puzzle);

            // List the words
            writeWordsToFind(doc, puzzle);

            // Page break after each puzzle
            doc.createParagraph().setPageBreak(true);
        }
    }

    private void writeWordsToFind(XWPFDocument doc, WordSearchPuzzle puzzle) {
        XWPFParagraph wordsPara = doc.createParagraph();
        XWPFRun wordsRun = wordsPara.createRun();
        wordsRun.setText("Words: " + String.join(", ", puzzle.getWordsAsStringList()));
        wordsRun.setFontSize(11);
    }

    private void writePuzzleTitle(XWPFDocument doc, int i) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setText("Word Search Puzzle " + (i + 1));
        run.setBold(true);
        run.setFontSize(18);
        run.addBreak();
    }

    private void writeSolutionsSection(XWPFDocument doc, List<WordSearchPuzzle> puzzles) {
        // Inline the solutions section title
        addSolutionsTitle(doc);
        int numCols = 2;
        int numRows = (int) Math.ceil(puzzles.size() / (double) numCols);
        XWPFTable table = doc.createTable(numRows, numCols);
        int puzzleIdx = 0;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                XWPFTableCell cell = table.getRow(row).getCell(col);
                if (puzzleIdx < puzzles.size()) {
                    WordSearchPuzzle puzzle = puzzles.get(puzzleIdx);
                    // Solution title
                    writeSolutionTitle(puzzleIdx, cell);
                    // Solution image
                    int size = 200; // smaller size for solutions
                    String imagePath = PuzzleProperties.getProperty("puzzles.output.dir")
                            + File.separator + puzzle.getName() + "-sol.png";
                    ParagraphAlignment alignment = ParagraphAlignment.LEFT;
                    addImage(doc, size, imagePath, alignment);
                } else {
                    // Remove empty cell's default paragraph
                    cell.removeParagraph(0);
                }
                puzzleIdx++;
            }
        }
        // Remove the first empty paragraph that Apache POI adds by default to the table
        table.removeRow(0);
    }

    private void addSolutionsTitle(XWPFDocument doc) {
        XWPFParagraph solTitle = doc.createParagraph();
        solTitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun solRun = solTitle.createRun();
        solRun.setText("Solutions");
        solRun.setBold(true);
        solRun.setFontSize(14);
        solRun.addBreak();
    }

    private void writeSolutionTitle(int puzzleIdx, XWPFTableCell cell) {
        XWPFParagraph titlePara = cell.addParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText("Solution to Puzzle " + (puzzleIdx + 1));
        titleRun.setFontSize(10);
        titleRun.setBold(true);
        titleRun.addBreak();
    }


    private void writePuzzle(XWPFDocument doc, WordSearchPuzzle puzzle) {
        // Insert the puzzle image instead of rendering the grid as a table
        int size = 400;
        String imagePath = PuzzleProperties.getProperty("puzzles.output.dir")
                             + File.separator + puzzle.getName() + ".png";
        ParagraphAlignment alignment = ParagraphAlignment.CENTER;
        addImage(doc, size, imagePath, alignment);
    }

    private void addImage(XWPFDocument doc, int size, String imagePath, ParagraphAlignment alignment) {
        try (java.io.FileInputStream is = new java.io.FileInputStream(imagePath)) {
            XWPFParagraph para = doc.createParagraph();
            para.setAlignment(alignment);
            XWPFRun run = para.createRun();
            run.addPicture(is, XWPFDocument.PICTURE_TYPE_PNG, imagePath, 
                org.apache.poi.util.Units.toEMU(size), 
                org.apache.poi.util.Units.toEMU(size)); 
            run.addBreak();
        } catch (Exception e) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            run.setText("[Image not found: " + imagePath + "]");
        }
        XWPFParagraph after = doc.createParagraph();
        after.createRun().addBreak();
    }

}
