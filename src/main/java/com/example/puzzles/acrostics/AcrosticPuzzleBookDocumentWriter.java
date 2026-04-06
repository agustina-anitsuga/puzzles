package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Position;
import com.example.puzzles.tools.PuzzleProperties;
import com.example.puzzles.tools.BookDocumentWriter;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AcrosticPuzzleBookDocumentWriter extends BookDocumentWriter {
    
    private static final int PARAGRAPH_SPACING = 400;
    private static final String FONT_FAMILY = "Georgia";
    private static final int NORMAL_TEXT_FONT_SIZE = 10;
    private static final int NUMBER_FONT_SIZE = 8;
    private static final int SECTION_TITLE_FONT_SIZE = 16;
    private static final int PAGE_TITLE_FONT_SIZE = 12;
    private static final String BORDER_COLOR = "808080";
    private static final int BORDER_THICKNESS_POINTS = 1;
    private static final int BORDER_SPACING = 0;
    private static final int GRID_COLUMNS = 20;
    private static final int GRID_CELL_WIDTH_TWIPS = 400; 
    private static final int GRID_ROW_HEIGHT = 200; 

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
        //addPuzzleImage(doc, imagePath);
        addPuzzleGridTable(doc, puzzle);
        addClues(doc, puzzle);
        addCharacterClues(doc, puzzle, clueImagePath);
        addPageBreak(doc);
    }

    private void addPageBreak(XWPFDocument doc) {
        XWPFParagraph pageBreak = doc.createParagraph();
        pageBreak.setPageBreak(true);
    }

    private void addCharacterClues(XWPFDocument doc, Puzzle puzzle, String cluesImagePath) {
        
        List<Character> characters = ((AcrosticPuzzle)puzzle).getSortedCharacters();
        int columnsPerRow = 30;

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
        title.setSpacingBefore(PARAGRAPH_SPACING);

        XWPFRun titleRun = title.createRun();
        titleRun.setText(PuzzleProperties.getProperty("label.characters"));
        titleRun.setBold(true);
        titleRun.setFontFamily(FONT_FAMILY);
        titleRun.setFontSize(PAGE_TITLE_FONT_SIZE);

        XWPFTable table = doc.createTable(rowCount, colCount);
        table.setTableAlignment(TableRowAlign.LEFT);

        CTTbl ctTbl = table.getCTTbl();
        CTTblPr tblPr = ctTbl.getTblPr() != null ? ctTbl.getTblPr() : ctTbl.addNewTblPr();

        int cellWidthTwips = GRID_CELL_WIDTH_TWIPS;
        CTTblWidth tblW = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblW.setType(STTblWidth.DXA);
        tblW.setW(BigInteger.valueOf((long) cellWidthTwips * colCount));

        clearTableBorders(tblPr);

        CTTblLayoutType layoutType = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
        layoutType.setType(STTblLayoutType.FIXED);

        CTTblGrid grid = ctTbl.getTblGrid() != null ? ctTbl.getTblGrid() : ctTbl.addNewTblGrid();
        while (grid.sizeOfGridColArray() > 0) {
            grid.removeGridCol(0);
        }

        for (int c = 0; c < colCount; c++) {
            CTTblGridCol gridCol = grid.addNewGridCol();
            gridCol.setW(BigInteger.valueOf(cellWidthTwips));
        }

        for (int r = 0; r < rowCount; r++) {
            XWPFTableRow row = table.getRow(r);
            row.setHeight(272);

            List<Character> rowChars = rows.get(r);

            for (int c = 0; c < colCount; c++) {
                XWPFTableCell cell = row.getCell(c);
                if (cell == null) {
                    cell = row.addNewTableCell();
                }

                String value = c < rowChars.size() ? String.valueOf(rowChars.get(c)) : "";

                setCellWidth(cell, cellWidthTwips);
                setCellVerticalCenter(cell);

                if (!value.isEmpty()) {
                    setCellBorders(cell);
                } else {
                    setNoCellBorders(cell);
                }

                while (cell.getParagraphs().size() > 0) {
                    cell.removeParagraph(0);
                }

                XWPFParagraph p = cell.addParagraph();
                p.setAlignment(ParagraphAlignment.CENTER);

                XWPFRun run = p.createRun();
                run.setText(value);
                run.setFontFamily(FONT_FAMILY);
                run.setFontSize(NORMAL_TEXT_FONT_SIZE);

            }
        }

        XWPFParagraph after = doc.createParagraph();
        after.setSpacingAfter(200);
    }

    /**
     * Adds a table to the Word document representing the puzzle grid, similar to the image grid.
     * Each row is a word, positioned so the intersecting letter aligns with the phrase.
     * The phrase is shown vertically in a separate table.
     */
    private void addPuzzleGridTable(XWPFDocument doc, AcrosticPuzzle puzzle) {
        List<Word> words = puzzle.getWords();
        Phrase phrase = puzzle.getPhrase();
        List<String> phraseChunks = phrase.getChunks();
        // Calculate the table dimensions with a fixed minimum width
        int numRows = words.size();
        int numCols = GRID_COLUMNS;
        int phraseCenter;
        Set<Integer> phraseColumns = new HashSet<>();

        while (true) {
            phraseCenter = numCols / 2;
            int minUsed = Integer.MAX_VALUE;
            int maxUsed = Integer.MIN_VALUE;
            for (Word word : words) {
                Position pos = word.getPosition();
                if (pos instanceof com.example.puzzles.model.AcrosticPuzzlePosition) {
                    List<Integer> intersections = ((com.example.puzzles.model.AcrosticPuzzlePosition) pos).getIntersections();
                    List<Integer> intersectingChunks = ((com.example.puzzles.model.AcrosticPuzzlePosition) pos).getIntersectingChunk();
                    if (intersections != null && !intersections.isEmpty() && intersectingChunks != null && !intersectingChunks.isEmpty()) {
                        int intersectionIdx = intersections.get(0);
                        int chunkIdx = intersectingChunks.get(0);
                        int chunkStartCol = (chunkIdx == 0) ? phraseCenter : (phraseCenter + phrase.getDistanceBetweenChunks());
                        int wordStartCol = chunkStartCol - intersectionIdx;
                        int wordEndCol = wordStartCol + word.getWord().length() - 1;
                        minUsed = Math.min(minUsed, wordStartCol - 1); // include number cell
                        maxUsed = Math.max(maxUsed, wordEndCol);
                    }
                }
            }
            int neededCols = maxUsed - minUsed + 1;
            if (neededCols <= numCols) {
                break;
            }
            numCols = neededCols;
        }

        phraseColumns.clear();
        phraseColumns.add(phraseCenter);
        if (phraseChunks.size() > 1) {
            phraseColumns.add(phraseCenter + phrase.getDistanceBetweenChunks());
        }

        // Create the table for words
        XWPFTable table = doc.createTable(numRows, numCols);
        table.setTableAlignment(TableRowAlign.LEFT);

        // Set up table formatting like in addCharacterClues
        CTTbl ctTbl = table.getCTTbl();
        CTTblPr tblPr = ctTbl.getTblPr() != null ? ctTbl.getTblPr() : ctTbl.addNewTblPr();

        CTTblWidth tblW = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblW.setType(STTblWidth.DXA);
        tblW.setW(BigInteger.valueOf((long) GRID_CELL_WIDTH_TWIPS * numCols));

        clearTableBorders(tblPr);

        CTTblLayoutType layoutType = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
        layoutType.setType(STTblLayoutType.FIXED);

        int cellWidthTwips = GRID_CELL_WIDTH_TWIPS;

        CTTblGrid grid = ctTbl.getTblGrid() != null ? ctTbl.getTblGrid() : ctTbl.addNewTblGrid();
        while (grid.sizeOfGridColArray() > 0) {
            grid.removeGridCol(0);
        }

        for (int c = 0; c < numCols; c++) {
            CTTblGridCol gridCol = grid.addNewGridCol();
            gridCol.setW(BigInteger.valueOf(cellWidthTwips));
        }

        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            String wordStr = word.getWord();
            XWPFTableRow row = table.getRow(i);
            row.setHeight(GRID_ROW_HEIGHT); // Same height for all puzzles

            // Get intersection index for this word
            int intersectionIdx = -1;
            int chunkIdx = 0;
            Position pos = word.getPosition();
            if (pos instanceof com.example.puzzles.model.AcrosticPuzzlePosition) {
                List<Integer> intersections = ((com.example.puzzles.model.AcrosticPuzzlePosition) pos).getIntersections();
                List<Integer> intersectingChunks = ((com.example.puzzles.model.AcrosticPuzzlePosition) pos).getIntersectingChunk();
                if (intersections != null && !intersections.isEmpty()) {
                    intersectionIdx = intersections.get(0);
                }
                if (intersectingChunks != null && !intersectingChunks.isEmpty()) {
                    chunkIdx = intersectingChunks.get(0);
                }
            }

            if (intersectionIdx >= 0) {
                int chunkStartCol = (chunkIdx == 0) ? phraseCenter : (phraseCenter + phrase.getDistanceBetweenChunks());
                int wordStartCol = chunkStartCol - intersectionIdx;
                int numberCol = wordStartCol - 1;

                // Place the number
                int tableNumberCol = numberCol;
                if (tableNumberCol >= 0 && tableNumberCol < numCols) {
                    XWPFTableCell numCell = row.getCell(tableNumberCol);
                    setCellWidth(numCell, cellWidthTwips);
                    setCellVerticalCenter(numCell);
                    XWPFParagraph numPara = numCell.getParagraphs().get(0);
                    XWPFRun numRun = numPara.createRun();
                    numRun.setText(String.valueOf(i + 1));
                    numRun.setFontFamily(FONT_FAMILY);
                    numRun.setBold(true);
                    numRun.setFontSize(NUMBER_FONT_SIZE);
                    setNoCellBorders(numCell); // Number cells have no borders
                }

                // Fill in the word's letters
                for (int j = 0; j < wordStr.length(); j++) {
                    int letterCol = wordStartCol + j;
                    int tableLetterCol = letterCol;
                    if (tableLetterCol >= 0 && tableLetterCol < numCols) {
                        XWPFTableCell cell = row.getCell(tableLetterCol);
                        setCellWidth(cell, cellWidthTwips);
                        setCellVerticalCenter(cell);
                        XWPFParagraph para = cell.getParagraphs().get(0);
                        XWPFRun run = para.createRun();
                        run.setText(String.valueOf(wordStr.charAt(j)));
                        // Bold the intersecting letter
                        if (j == intersectionIdx) {
                            run.setBold(true);
                        }
                        setCellBorders(cell); // Letter cells have borders
                    }
                }
            }

            // Set formatting for all cells in this row, including empty ones
            for (int c = 0; c < numCols; c++) {
                XWPFTableCell cell = row.getCell(c);
                setCellWidth(cell, cellWidthTwips);
                setCellVerticalCenter(cell);
                // Check if cell has content
                String cellText = cell.getText().trim();
                if (cellText.isEmpty()) {
                    setNoCellBorders(cell); // Empty cells have no borders
                } else if (cellText.matches("\\d+")) {
                    setNoCellBorders(cell); // Number cells have no borders
                } else {
                    // Check if this cell is in any phrase column
                        int actualCol = c;
                    if (phraseColumns.contains(actualCol)) {
                        setBoldCellBorders(cell); // Phrase column cells have bold borders
                    } else {
                        setCellBorders(cell); // Other cells with content have normal borders
                    }
                }
            }
        }

    }

    private void setNoCellBorders(XWPFTableCell cell) {
        // For empty cells, we can choose to have no borders or very light borders
        CTTc cttc = cell.getCTTc();
        CTTcPr tcPr = cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();
        CTTcBorders borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();

        CTBorder top = borders.isSetTop() ? borders.getTop() : borders.addNewTop();
        top.setVal(STBorder.NONE);

        CTBorder bottom = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();
        bottom.setVal(STBorder.NONE);

        CTBorder left = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
        left.setVal(STBorder.NONE);

        CTBorder right = borders.isSetRight() ? borders.getRight() : borders.addNewRight();
        right.setVal(STBorder.NONE);
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

    private static void clearTableBorders(CTTblPr tblPr) {
        CTTblBorders tblBorders = tblPr.isSetTblBorders() ? tblPr.getTblBorders() : tblPr.addNewTblBorders();

        CTBorder top = tblBorders.isSetTop() ? tblBorders.getTop() : tblBorders.addNewTop();
        top.setVal(STBorder.NONE);

        CTBorder bottom = tblBorders.isSetBottom() ? tblBorders.getBottom() : tblBorders.addNewBottom();
        bottom.setVal(STBorder.NONE);

        CTBorder left = tblBorders.isSetLeft() ? tblBorders.getLeft() : tblBorders.addNewLeft();
        left.setVal(STBorder.NONE);

        CTBorder right = tblBorders.isSetRight() ? tblBorders.getRight() : tblBorders.addNewRight();
        right.setVal(STBorder.NONE);

        CTBorder insideH = tblBorders.isSetInsideH() ? tblBorders.getInsideH() : tblBorders.addNewInsideH();
        insideH.setVal(STBorder.NONE);

        CTBorder insideV = tblBorders.isSetInsideV() ? tblBorders.getInsideV() : tblBorders.addNewInsideV();
        insideV.setVal(STBorder.NONE);
    }

    private static void setCellBorders(XWPFTableCell cell) {
        CTTc cttc = cell.getCTTc();
        CTTcPr tcPr = cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();
        CTTcBorders borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();

        // Top border
        CTBorder top = borders.isSetTop() ? borders.getTop() : borders.addNewTop();
        top.setVal(STBorder.SINGLE);
        top.setSz(BigInteger.valueOf(BORDER_THICKNESS_POINTS * 8L));
        top.setSpace(BigInteger.valueOf(BORDER_SPACING));
        top.setColor(BORDER_COLOR);

        // Bottom border
        CTBorder bottom = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();
        bottom.setVal(STBorder.SINGLE);
        bottom.setSz(BigInteger.valueOf(BORDER_THICKNESS_POINTS * 8L));
        bottom.setSpace(BigInteger.valueOf(BORDER_SPACING));
        bottom.setColor(BORDER_COLOR);

        // Left border
        CTBorder left = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
        left.setVal(STBorder.SINGLE);
        left.setSz(BigInteger.valueOf(BORDER_THICKNESS_POINTS * 8L));
        left.setSpace(BigInteger.valueOf(BORDER_SPACING));
        left.setColor(BORDER_COLOR);

        // Right border
        CTBorder right = borders.isSetRight() ? borders.getRight() : borders.addNewRight();
        right.setVal(STBorder.SINGLE);
        right.setSz(BigInteger.valueOf(BORDER_THICKNESS_POINTS * 8L));
        right.setSpace(BigInteger.valueOf(BORDER_SPACING));
        right.setColor(BORDER_COLOR);
    }

    private static void setBoldCellBorders(XWPFTableCell cell) {
        CTTc cttc = cell.getCTTc();
        CTTcPr tcPr = cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();
        CTTcBorders borders = tcPr.isSetTcBorders() ? tcPr.getTcBorders() : tcPr.addNewTcBorders();

        // Top border - bolder
        CTBorder top = borders.isSetTop() ? borders.getTop() : borders.addNewTop();
        top.setVal(STBorder.SINGLE);
        top.setSz(BigInteger.valueOf(BORDER_THICKNESS_POINTS * 16L)); // Double thickness
        top.setSpace(BigInteger.valueOf(BORDER_SPACING));
        top.setColor(BORDER_COLOR);

        // Bottom border - bolder
        CTBorder bottom = borders.isSetBottom() ? borders.getBottom() : borders.addNewBottom();
        bottom.setVal(STBorder.SINGLE);
        bottom.setSz(BigInteger.valueOf(BORDER_THICKNESS_POINTS * 16L)); // Double thickness
        bottom.setSpace(BigInteger.valueOf(BORDER_SPACING));
        bottom.setColor(BORDER_COLOR);

        // Left border - bolder
        CTBorder left = borders.isSetLeft() ? borders.getLeft() : borders.addNewLeft();
        left.setVal(STBorder.SINGLE);
        left.setSz(BigInteger.valueOf(BORDER_THICKNESS_POINTS * 16L)); // Double thickness
        left.setSpace(BigInteger.valueOf(BORDER_SPACING));
        left.setColor(BORDER_COLOR);

        // Right border - bolder
        CTBorder right = borders.isSetRight() ? borders.getRight() : borders.addNewRight();
        right.setVal(STBorder.SINGLE);
        right.setSz(BigInteger.valueOf(BORDER_THICKNESS_POINTS * 16L)); // Double thickness
        right.setSpace(BigInteger.valueOf(BORDER_SPACING));
        right.setColor(BORDER_COLOR);
    }

    private void addClues(XWPFDocument doc, Puzzle puzzle) {
        // Write the label in a different font and size
        XWPFParagraph labelParagraph = doc.createParagraph();
        labelParagraph.setSpacingBefore(PARAGRAPH_SPACING);
        labelParagraph.setSpacingAfter(PARAGRAPH_SPACING);

        XWPFRun labelRun = labelParagraph.createRun();
        labelRun.setText(""+PuzzleProperties.getProperty("label.clues"));
        labelRun.setFontFamily(FONT_FAMILY);
        labelRun.setFontSize(PAGE_TITLE_FONT_SIZE);
        labelRun.setBold(true);

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
