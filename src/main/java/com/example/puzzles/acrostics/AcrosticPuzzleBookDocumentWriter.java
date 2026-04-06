package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import com.example.puzzles.tools.PuzzleProperties;
import com.example.puzzles.tools.BookDocumentWriter;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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
        
        List<Character> characters = ((AcrosticPuzzle)puzzle).getSortedCharacters();
        int columnsPerRow = 25;

        if (doc == null || characters == null || characters.isEmpty() || columnsPerRow <= 0) {
            return;
        }

        List<List<Character>> rows = splitIntoRows(characters, columnsPerRow);
        int rowCount = rows.size();
        int colCount = rows.stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        if (colCount == 0) {
            return;
        }

        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun titleRun = title.createRun();
        titleRun.setText("Characters");
        titleRun.setBold(true);
        titleRun.setFontSize(12);

        XWPFTable table = doc.createTable(rowCount, colCount);
        table.setTableAlignment(TableRowAlign.LEFT);

        CTTbl ctTbl = table.getCTTbl();
        CTTblPr tblPr = ctTbl.getTblPr() != null ? ctTbl.getTblPr() : ctTbl.addNewTblPr();

        CTTblWidth tblW = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblW.setType(STTblWidth.DXA);
        tblW.setW(BigInteger.valueOf(9000));

        CTTblLayoutType layoutType = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
        layoutType.setType(STTblLayoutType.FIXED);

        int cellWidthTwips = 320;

        CTTblGrid grid = ctTbl.getTblGrid() != null ? ctTbl.getTblGrid() : ctTbl.addNewTblGrid();
        while (grid.sizeOfGridColArray() > 0) {
            grid.removeGridCol(0);
        }

        for (int c = 0; c < colCount; c++) {
            CTTblGridCol gridCol = grid.addNewGridCol();
            gridCol.setW(BigInteger.valueOf(cellWidthTwips));
        }

        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "808080");
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "808080");
        table.setTopBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "808080");
        table.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "808080");
        table.setLeftBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "808080");
        table.setRightBorder(XWPFTable.XWPFBorderType.SINGLE, 4, 0, "808080");

        for (int r = 0; r < rowCount; r++) {
            XWPFTableRow row = table.getRow(r);
            row.setHeight(340);

            List<Character> rowChars = rows.get(r);

            for (int c = 0; c < colCount; c++) {
                XWPFTableCell cell = row.getCell(c);
                if (cell == null) {
                    cell = row.addNewTableCell();
                }

                String value = c < rowChars.size() ? String.valueOf(rowChars.get(c)) : "";

                setCellWidth(cell, cellWidthTwips);
                setCellVerticalCenter(cell);

                while (cell.getParagraphs().size() > 0) {
                    cell.removeParagraph(0);
                }

                XWPFParagraph p = cell.addParagraph();
                p.setAlignment(ParagraphAlignment.CENTER);

                XWPFRun run = p.createRun();
                run.setText(value);
                run.setFontFamily("Calibri");
                run.setFontSize(10);

                cell.setColor("F2F2F2");
            }
        }

        XWPFParagraph after = doc.createParagraph();
        after.setSpacingAfter(200);
    }

    private static List<List<Character>> splitIntoRows(List<Character> characters, int columnsPerRow) {
        List<List<Character>> rows = new ArrayList<>();

        for (int i = 0; i < characters.size(); i += columnsPerRow) {
            rows.add(new ArrayList<>(
                    characters.subList(i, Math.min(i + columnsPerRow, characters.size()))
            ));
        }

        return rows;
    }

    private static void setCellWidth(XWPFTableCell cell, int widthTwips) {
        CTTc cttc = cell.getCTTc();
        CTTcPr tcPr = cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();

        CTTblWidth tcW = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
        tcW.setType(STTblWidth.DXA);
        tcW.setW(BigInteger.valueOf(widthTwips));
    }

    private static void setCellVerticalCenter(XWPFTableCell cell) {
        CTTc cttc = cell.getCTTc();
        CTTcPr tcPr = cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();

        CTVerticalJc vAlign = tcPr.isSetVAlign() ? tcPr.getVAlign() : tcPr.addNewVAlign();
        vAlign.setVal(STVerticalJc.CENTER);
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
        descRun.setText("Finding the words will reveal a phrase from '"+puzzle.getPhrase().getSource()+"' by "+puzzle.getPhrase().getAuthor()+".");
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
