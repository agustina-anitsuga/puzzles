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
            writeGrid(doc, puzzle, false);

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
        // Solutions section
        writeSolutionsTitle(doc);

        for (int i = 0; i < puzzles.size(); i++) {
            WordSearchPuzzle puzzle = puzzles.get(i);
            writeSolutionTitle(doc, i);
            // Write the solution grid in smaller font
            writeGrid(doc, puzzle, true);
        }
    }

    private void writeSolutionTitle(XWPFDocument doc, int i) {
        XWPFParagraph solPuzzleTitle = doc.createParagraph();
        solPuzzleTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun solPuzzleRun = solPuzzleTitle.createRun();
        solPuzzleRun.setText("Solution to Puzzle " + (i + 1));
        solPuzzleRun.setFontSize(10);
        solPuzzleRun.setBold(true);
        solPuzzleRun.addBreak();
    }

    private void writeSolutionsTitle(XWPFDocument doc) {
        XWPFParagraph solTitle = doc.createParagraph();
        solTitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun solRun = solTitle.createRun();
        solRun.setText("Solutions");
        solRun.setBold(true);
        solRun.setFontSize(14);
        solRun.addBreak();
    }

    private void writeGrid(XWPFDocument doc, WordSearchPuzzle puzzle, boolean solution) {
        // Insert the puzzle image instead of rendering the grid as a table
        int size = 400;
        String imagePath = PuzzleProperties.getProperty("puzzles.output.dir")
                             + File.separator + puzzle.getName();
        if(!solution) {
            imagePath += ".png";
        } else {
            imagePath += "-sol.png";
            size = 200;
        }

        try (java.io.FileInputStream is = new java.io.FileInputStream(imagePath)) {
            XWPFParagraph para = doc.createParagraph();
            para.setAlignment(ParagraphAlignment.CENTER);
            if(solution) {
                para.setAlignment(ParagraphAlignment.LEFT);
            }
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
