package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.PuzzleProperties;
import com.example.puzzles.tools.BookDocumentWriter;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class AcrosticPuzzleBookDocumentWriter extends BookDocumentWriter {
    
    private static final String FONT_FAMILY = "Georgia";
    private static final int NORMAL_TEXT_FONT_SIZE = 12;
    private static final int SECTION_TITLE_FONT_SIZE = 18;
    private static final int PAGE_TITLE_FONT_SIZE = 14;

    public XWPFDocument createDocument(List<AcrosticPuzzle> puzzles, List<String> imagePaths, List<String> clueImagePaths) throws Exception {
        XWPFDocument doc = super.createDocument();
        addPuzzlePages(doc, puzzles, imagePaths, clueImagePaths);
        addSolutionsSection(doc, puzzles);
        super.endDocument(doc); 
        return doc;
    }

    private void addPuzzlePages(XWPFDocument doc, List<AcrosticPuzzle> puzzles, List<String> imagePaths, List<String> clueImagePaths) throws Exception {
        for (int i = 0; i < puzzles.size(); i++) {
            AcrosticPuzzle puzzle = puzzles.get(i);
            addPuzzlePage(doc, puzzle, imagePaths.get(i), clueImagePaths.get(i), i + 1);
        }
    }

    private void addPuzzlePage(XWPFDocument doc, AcrosticPuzzle puzzle, String imagePath, String clueImagePath, int puzzleNumber) throws Exception {
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
        titleRun.addBreak();
        titleRun.setText(PuzzleProperties.getProperty("label.characters"));
        titleRun.setFontFamily(FONT_FAMILY);
        titleRun.setFontSize(PAGE_TITLE_FONT_SIZE);
        titleRun.setBold(true);
        titleRun.addBreak();

        // Add a description below the title
        //XWPFParagraph descPara = doc.createParagraph();
        //XWPFRun descRun = descPara.createRun();
        //descRun.setText("These are the characters used in the words of the acrostic puzzle.");
        //descRun.setFontFamily(FONT_FAMILY);
        //descRun.setFontSize(NORMAL_TEXT_FONT_SIZE);
        //descRun.addBreak();

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
        labelRun.setText(""+PuzzleProperties.getProperty("label.clues") + ":");
        labelRun.setFontFamily(FONT_FAMILY);
        labelRun.setFontSize(PAGE_TITLE_FONT_SIZE);
        labelRun.setBold(true);

        // Now write the clues in a different font, one per line
        XWPFParagraph cluesParagraph = doc.createParagraph();
        XWPFRun cluesRun = cluesParagraph.createRun();
        cluesRun.setFontFamily(FONT_FAMILY);
        cluesRun.setFontSize(NORMAL_TEXT_FONT_SIZE);
        List<Word> words = puzzle.getWords();

        // Create a simple manual numbered list so numbering restarts at 1 for each puzzle
        int wordNum = 1;
        for (Word word : words) {
            XWPFParagraph para = doc.createParagraph();
            para.setAlignment(ParagraphAlignment.LEFT);

            // Set a hanging indent
            para.setIndentationLeft(600);
            para.setIndentationHanging(600);

            XWPFRun run = para.createRun();
            run.setFontFamily(FONT_FAMILY);
            run.setFontSize(NORMAL_TEXT_FONT_SIZE);
            run.setText((wordNum++) + ". " + word.getDefinition());
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
            int widthInInches = widthInPixels / DPI;
            int heightInInches = heightInPixels / DPI;
            int widthInEmu = Units.toEMU(widthInPixels);
            int heightInEmu = Units.toEMU(heightInPixels);
            double maxWidthInInches = 4;
            double maxHeightInInches = 6;
            double scaleWidth = 1;
            double scaleHeight = 1;
            if (widthInInches > maxWidthInInches) {
                scaleWidth = (double) maxWidthInInches / widthInInches;
            }
            if(heightInInches > maxHeightInInches){
                scaleHeight = (double) maxHeightInInches / heightInInches;
            }
            double scale = Math.min(scaleWidth, scaleHeight);
            widthInEmu = Units.toEMU(widthInPixels * scale);
            heightInEmu = Units.toEMU(heightInPixels * scale);
            XWPFParagraph imgPara = doc.createParagraph();
            imgPara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun imgRun = imgPara.createRun();
            imgRun.addPicture(is, Document.PICTURE_TYPE_PNG, imagePath, widthInEmu, heightInEmu);
            imgRun.addBreak(BreakType.PAGE); // Add page break after the image
        }
    }

    private XWPFParagraph addPuzzleTitle(XWPFDocument doc, AcrosticPuzzle puzzle, int puzzleNumber) {
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

    private void addSolutionsSection(XWPFDocument doc, List<AcrosticPuzzle> puzzles) {
        XWPFParagraph solTitle = doc.createParagraph();
        solTitle.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun solRun = solTitle.createRun();
        solRun.setText(PuzzleProperties.getProperty("label.solutions"));
        solRun.setBold(true);
        solRun.setFontSize(SECTION_TITLE_FONT_SIZE);
        solRun.addBreak();

        for (int i = 0; i < puzzles.size(); i++) {
            AcrosticPuzzle puzzle = puzzles.get(i);
            XWPFParagraph solPara = doc.createParagraph();
            XWPFRun solParaRun = solPara.createRun();
            solParaRun.setText(PuzzleProperties.getProperty("label.puzzle")+" " + (i + 1) + ": " + puzzle.getPhrase().getPhrase());
            solParaRun.addBreak();
            solParaRun.setText(PuzzleProperties.getProperty("label.source")+": " + puzzle.getPhrase().getSource());
            solParaRun.addBreak();
            solParaRun.setText(PuzzleProperties.getProperty("label.author")+": "+ puzzle.getPhrase().getAuthor());
            solParaRun.addBreak();
            solParaRun.setText(PuzzleProperties.getProperty("label.words")+": ");
            solParaRun.addBreak();
            // Numbered bullet list for words
            int wordNum = 1;
            for (Word word : puzzle.getWords()) {
                solParaRun.setText(wordNum++ + ". " + word.getWord() + "; ");
            }
            solParaRun.addBreak();
        }
    }

}
