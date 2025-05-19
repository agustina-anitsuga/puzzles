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
    
    private static final String FONT_FAMILY = "Arial";
    private static final int NORMAL_TEXT_FONT_SIZE = 12;
    private static final int BOOK_TITLE_FONT_SIZE = 28;
    private static final int SECTION_TITLE_FONT_SIZE = 18;
    private static final int PAGE_TITLE_FONT_SIZE = 14;

    public XWPFDocument createDocument(List<Puzzle> puzzles, List<String> imagePaths, List<String> clueImagePaths) throws Exception {
        XWPFDocument doc = new XWPFDocument();        
        setPageFormat(doc);
        addTitlePage(doc);
        addPuzzlePages(doc, puzzles, imagePaths, clueImagePaths);
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

    private void addPuzzlePages(XWPFDocument doc, List<Puzzle> puzzles, List<String> imagePaths, List<String> clueImagePaths) throws Exception {
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            addPuzzlePage(doc, puzzle, imagePaths.get(i), clueImagePaths.get(i), i + 1);
        }
    }

    private void addPuzzlePage(XWPFDocument doc, Puzzle puzzle, String imagePath, String clueImagePath, int puzzleNumber) throws Exception {
        addPuzzleTitle(doc, puzzle, puzzleNumber);
        addPuzzleImage(doc, imagePath);
        addClues(doc, puzzle);
        addCharacterClues(doc, puzzle, clueImagePath);
    }

    private void addCharacterClues(XWPFDocument doc, Puzzle puzzle, String cluesImagePath) {
        // Add a title for the character clues section
        XWPFParagraph titlePara = doc.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(PuzzleProperties.getProperty("label.characters"));
        titleRun.setFontFamily(FONT_FAMILY);
        titleRun.setFontSize(PAGE_TITLE_FONT_SIZE);
        titleRun.setBold(true);
        titleRun.addBreak();

        // Add a description below the title
        XWPFParagraph descPara = doc.createParagraph();
        XWPFRun descRun = descPara.createRun();
        descRun.setText("These are the characters used in the words of the acrostic puzzle.");
        descRun.setFontFamily(FONT_FAMILY);
        descRun.setFontSize(NORMAL_TEXT_FONT_SIZE);
        descRun.addBreak();

        try (FileInputStream is = new FileInputStream(cluesImagePath)) {
            javax.imageio.ImageIO.setUseCache(false);
            java.awt.image.BufferedImage bimg = javax.imageio.ImageIO.read(new java.io.File(cluesImagePath));
            int width = bimg.getWidth();
            int height = bimg.getHeight();
            int scale = 1;
            int scaledWidth = (int) (width * scale);
            int scaledHeight = (int) (height * scale);

            XWPFParagraph imgPara = doc.createParagraph();
            imgPara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun imgRun = imgPara.createRun();
            imgRun.addPicture(is, Document.PICTURE_TYPE_PNG, cluesImagePath, Units.toEMU(scaledWidth), Units.toEMU(scaledHeight));
            imgRun.addBreak(BreakType.PAGE);
        } catch (Exception e) {
            // Fallback: write a warning if image is missing
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            run.setText("[Character clues image not found: " + cluesImagePath + "]");
            run.addBreak(BreakType.PAGE);
        }
    }

    private void addClues(XWPFDocument doc, Puzzle puzzle) {
        // Write the label in a different font and size
        XWPFParagraph labelParagraph = doc.createParagraph();
        XWPFRun labelRun = labelParagraph.createRun();
        labelRun.setText(PuzzleProperties.getProperty("label.clues") + ":");
        labelRun.setFontFamily(FONT_FAMILY);
        labelRun.setFontSize(PAGE_TITLE_FONT_SIZE);
        labelRun.setBold(true);
        labelRun.addBreak(); // blank line after the label

        // Now write the clues in a different font, one per line
        XWPFParagraph cluesParagraph = doc.createParagraph();
        XWPFRun cluesRun = cluesParagraph.createRun();
        cluesRun.addBreak(); // extra blank line between title and clues
        cluesRun.setFontFamily(FONT_FAMILY);
        cluesRun.setFontSize(NORMAL_TEXT_FONT_SIZE);
        int clueNum = 1;
        for (Word word : puzzle.getWords()) {
            cluesRun.setText(clueNum++ + ". " + word.getDefinition());
            cluesRun.addBreak();
        }
        cluesRun.addBreak();
    }

    private void addPuzzleImage(XWPFDocument doc, String imagePath)
            throws InvalidFormatException, IOException, FileNotFoundException {
        // Insert image with aspect ratio preserved, max width 7 inches
        try (FileInputStream is = new FileInputStream(imagePath)) {
            javax.imageio.ImageIO.setUseCache(false);
            java.awt.image.BufferedImage bimg = javax.imageio.ImageIO.read(new java.io.File(imagePath));
            int widthInPixels = bimg.getWidth();
            int heightInPixels = bimg.getHeight();
            int DPI = 96;
            int widthInInches = bimg.getWidth() / DPI;
            int widthInEmu = Units.toEMU(widthInPixels);
            int heightInEmu = Units.toEMU(heightInPixels);
            double maxWidthInInches = 4.5;
            if (widthInInches > maxWidthInInches) {
                double scale = (double) 7 / widthInInches;
                widthInEmu = Units.toEMU(widthInPixels * scale);
                heightInEmu = Units.toEMU(heightInPixels * scale);
            }
            XWPFParagraph imgPara = doc.createParagraph();
            imgPara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun imgRun = imgPara.createRun();
            imgRun.addPicture(is, Document.PICTURE_TYPE_PNG, imagePath, widthInEmu, heightInEmu);
            imgRun.addBreak(BreakType.PAGE);
        }
    }

    private XWPFParagraph addPuzzleTitle(XWPFDocument doc, Puzzle puzzle, int puzzleNumber) {
        XWPFParagraph pTitle = doc.createParagraph();
        pTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun pRun = pTitle.createRun();
        pRun.setText(PuzzleProperties.getProperty("label.puzzle")+" " + puzzleNumber);
        pRun.setBold(true);
        pRun.setFontSize(SECTION_TITLE_FONT_SIZE);
        pRun.setFontFamily(FONT_FAMILY);
        pRun.addBreak();

        XWPFParagraph pDesc = doc.createParagraph();
        XWPFRun descRun = pDesc.createRun();
        descRun.setText("Finding the words will reveal a phrase from '"+puzzle.getPhrase().getSource()+"'' by "+puzzle.getPhrase().getAuthor()+".");
        descRun.setBold(false);
        descRun.setFontSize(NORMAL_TEXT_FONT_SIZE);
        descRun.setFontFamily(FONT_FAMILY);
        descRun.addBreak();
        return pDesc;
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
            solParaRun.setText(PuzzleProperties.getProperty("label.puzzle")+" " + (i + 1) + ": " + puzzle.getPhrase().getPhrase());
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
