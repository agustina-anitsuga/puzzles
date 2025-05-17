package com.example.puzzles;

import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;

import java.io.FileInputStream;
import java.util.List;
import java.util.stream.Collectors;

public class PuzzleBookDocumentWriter {
    
    public XWPFDocument createDocument(List<Puzzle> puzzles, List<String> imagePaths) throws Exception {
        XWPFDocument doc = new XWPFDocument();
        addTitlePage(doc);
        addPuzzlePages(doc, puzzles, imagePaths);
        addSolutionsSection(doc, puzzles);
        return doc;
    }

    private void addTitlePage(XWPFDocument doc) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = title.createRun();
        run.setText("Puzzle Book");
        run.setBold(true);
        run.setFontSize(28);
        run.addBreak(BreakType.PAGE);
    }

    private void addPuzzlePages(XWPFDocument doc, List<Puzzle> puzzles, List<String> imagePaths) throws Exception {
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            addPuzzlePage(doc, puzzle, imagePaths.get(i), i + 1);
        }
    }

    private void addPuzzlePage(XWPFDocument doc, Puzzle puzzle, String imagePath, int puzzleNumber) throws Exception {
        XWPFParagraph pTitle = doc.createParagraph();
        pTitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun pRun = pTitle.createRun();
        pRun.setText("Puzzle " + puzzleNumber);
        pRun.setBold(true);
        pRun.setFontSize(20);
        pRun.addBreak();

        // Insert image
        try (FileInputStream is = new FileInputStream(imagePath)) {
            XWPFParagraph imgPara = doc.createParagraph();
            imgPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun imgRun = imgPara.createRun();
            imgRun.addPicture(is, Document.PICTURE_TYPE_PNG, imagePath, Units.toEMU(400), Units.toEMU(400));
        }

        // Clues
        XWPFParagraph cluesPara = doc.createParagraph();
        XWPFRun cluesRun = cluesPara.createRun();
        cluesRun.setText("Clues:");
        cluesRun.setBold(true);
        cluesRun.addBreak();
        int clueNum = 1;
        for (Word word : puzzle.getWords()) {
            cluesRun.setText(clueNum++ + ". " + word.getDefinition());
            cluesRun.addBreak();
        }
        cluesRun.addBreak();

        // Sorted characters from words 
        XWPFParagraph charactersPara = doc.createParagraph();
        XWPFRun charactersRun = charactersPara.createRun();
        charactersRun.setText("Characters: " + getSortedLetters(puzzle));
        charactersRun.setItalic(true);
        charactersRun.addBreak(BreakType.PAGE);
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
        solTitle.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun solRun = solTitle.createRun();
        solRun.setText("Solutions");
        solRun.setBold(true);
        solRun.setFontSize(24);
        solRun.addBreak();

        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            XWPFParagraph solPara = doc.createParagraph();
            XWPFRun solParaRun = solPara.createRun();
            solParaRun.setText("Puzzle " + (i + 1) + ": " + puzzle.getPhrase().getPhrase());
            solParaRun.addBreak();
            solParaRun.setText("Book: " + puzzle.getPhrase().getBook());
            solParaRun.addBreak();
            solParaRun.setText("Author: " + puzzle.getPhrase().getAuthor());
            solParaRun.addBreak();
            solParaRun.setText("Words:");
            solParaRun.addBreak();
            int wordNum = 1;
            for (Word word : puzzle.getWords()) {
                solParaRun.setText(wordNum++ + ". " + word.getWord());
                solParaRun.addBreak();
            }
            solParaRun.addBreak(); // blank line between solutions
        }
    }
}
