package com.example.puzzles.acrostics;

import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.PuzzleProperties;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class AcrosticPuzzleBookDocumentWriter {
    
    private static final int NORMAL_TEXT_FONT_SIZE = 12;
    private static final int BOOK_TITLE_FONT_SIZE = 28;
    private static final int SECTION_TITLE_FONT_SIZE = 18;

    public XWPFDocument createDocument(List<Puzzle> puzzles, List<String> imagePaths) throws Exception {
        XWPFDocument doc = new XWPFDocument();        
        setPageFormat(doc);
        addTitlePage(doc);
        addPuzzlePages(doc, puzzles, imagePaths);
        addSolutionsSection(doc, puzzles);
        return doc;
    }

    private void setPageFormat(XWPFDocument doc) {
        // Set page size to 8.5 x 11 inches (US Letter)
        CTBody body = doc.getDocument().getBody();
        if (body.isSetSectPr()) {
            CTSectPr sectPr = body.getSectPr();
            if (sectPr.isSetPgSz()) {
                sectPr.getPgSz().setW(BigInteger.valueOf(12240)); // 8.5 * 1440
                sectPr.getPgSz().setH(BigInteger.valueOf(15840)); // 11 * 1440
            } else {
                CTPageSz pageSz = sectPr.addNewPgSz();
                pageSz.setW(BigInteger.valueOf(12240));
                pageSz.setH(BigInteger.valueOf(15840));
            }
        } else {
            CTSectPr sectPr = body.addNewSectPr();
            CTPageSz pageSz = sectPr.addNewPgSz();
            pageSz.setW(BigInteger.valueOf(12240));
            pageSz.setH(BigInteger.valueOf(15840));
        }
    }

    private void addTitlePage(XWPFDocument doc) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setText(PuzzleProperties.getProperty("label.bookTitle"));
        run.setBold(true);
        run.setFontSize(BOOK_TITLE_FONT_SIZE);
        run.addBreak(BreakType.PAGE);
    }

    private void addPuzzlePages(XWPFDocument doc, List<Puzzle> puzzles, List<String> imagePaths) throws Exception {
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            addPuzzlePage(doc, puzzle, imagePaths.get(i), i + 1);
        }
    }

    private void addPuzzlePage(XWPFDocument doc, Puzzle puzzle, String imagePath, int puzzleNumber) throws Exception {
        
        addPuzzleTitle(doc, puzzle, puzzleNumber);
        addPuzzleImage(doc, imagePath);
        addClues(doc, puzzle);

        // Sorted characters from words 
        XWPFParagraph charactersPara = doc.createParagraph();
        XWPFRun charactersRun = charactersPara.createRun();
        charactersRun.setText(PuzzleProperties.getProperty("label.characters")+": " + getSortedLetters(puzzle));
        charactersRun.addBreak(BreakType.PAGE);
    }

    private void addClues(XWPFDocument doc, Puzzle puzzle) {
        // Clues in two columns
        List<Word> words = puzzle.getWords();
        int total = words.size();
        int mid = (total + 1) / 2;
        XWPFTable table = doc.createTable();
        
        table.removeBorders();
        StringBuffer rightSb = new StringBuffer();
        StringBuffer leftSb = new StringBuffer();
        XWPFTableRow row = table.createRow() ;
        for (int i = 0; i < mid; i++) {
            String left = (i < words.size()) ? (i+1) + ". " + words.get(i).getDefinition() : "";
            leftSb.append(left).append("\n\n");
            String right = (i+mid < words.size()) ? (i+mid+1) + ". " + words.get(i+mid).getDefinition() : "";
            rightSb.append(right).append("\n\n");
        
        }
        row.getCell(0).setText(leftSb.toString());
        row.createCell().setText(rightSb.toString());
    }

    private void addPuzzleImage(XWPFDocument doc, String imagePath)
            throws InvalidFormatException, IOException, FileNotFoundException {
        // Insert image
        try (FileInputStream is = new FileInputStream(imagePath)) {
            XWPFParagraph imgPara = doc.createParagraph();
            imgPara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun imgRun = imgPara.createRun();
            imgRun.addPicture(is, Document.PICTURE_TYPE_PNG, imagePath, Units.toEMU(400), Units.toEMU(400));
        }
    }

    private XWPFParagraph addPuzzleTitle(XWPFDocument doc, Puzzle puzzle, int puzzleNumber) {
        XWPFParagraph pTitle = doc.createParagraph();
        pTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun pRun = pTitle.createRun();
        pRun.setText(PuzzleProperties.getProperty("label.puzzle")+" " + puzzleNumber);
        pRun.setBold(true);
        pRun.setFontSize(SECTION_TITLE_FONT_SIZE);
        pRun.addBreak();

        XWPFParagraph pDesc = doc.createParagraph();
        XWPFRun descRun = pDesc.createRun();
        descRun.setText("Finding the words will reveal a phrase from '"+puzzle.getPhrase().getSource()+"'' by "+puzzle.getPhrase().getAuthor()+".");
        descRun.setBold(false);
        descRun.setFontSize(NORMAL_TEXT_FONT_SIZE);
        descRun.addBreak();
        return pDesc;
    }

    // Helper method to get sorted letters from the puzzle's words (copied from PuzzleFileWriter)
    private String getSortedLetters(Puzzle puzzle) {
        StringBuilder sb = new StringBuilder();
        puzzle.getWords().stream()
            .flatMap(word -> word.getWord().chars().mapToObj(c -> (char) c))
            .sorted()
            .forEach(c -> sb.append(c).append(" "));
        return sb.toString().trim();
    }

    private void addSolutionsSection(XWPFDocument doc, List<Puzzle> puzzles) {
        XWPFParagraph solTitle = doc.createParagraph();
        solTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun solRun = solTitle.createRun();
        solRun.setText(PuzzleProperties.getProperty("label.solutions"));
        solRun.setBold(true);
        solRun.setFontSize(SECTION_TITLE_FONT_SIZE);
        solRun.addBreak();

        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            XWPFParagraph solPara = doc.createParagraph();
            XWPFRun solParaRun = solPara.createRun();
            solParaRun.setText(PuzzleProperties.getProperty("label.puzzle")+": " + (i + 1) + ": " + puzzle.getPhrase().getPhrase());
            solParaRun.addBreak();
            solParaRun.setText(PuzzleProperties.getProperty("label.source")+": " + puzzle.getPhrase().getSource());
            solParaRun.addBreak();
            solParaRun.setText(PuzzleProperties.getProperty("label.author")+": "+ puzzle.getPhrase().getAuthor());
            solParaRun.addBreak();
            solParaRun.setText(PuzzleProperties.getProperty("label.words")+": ");
            int wordNum = 1;
            for (Word word : puzzle.getWords()) {
                solParaRun.setText(wordNum++ + ". " + word.getWord() + "; ");
            }
            solParaRun.addBreak(); // blank line between solutions
        }
    }
}
