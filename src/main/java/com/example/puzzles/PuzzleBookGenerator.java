package com.example.puzzles;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Puzzle;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PuzzleBookGenerator {
    private static final int PUZZLE_COUNT = 5;
    private static final String PHRASES_FILE = "phrases/Iconic_Book_Quotes.xlsx";
    private static final String OUTPUT_DOCX = "PuzzleBook.docx";
    private static final String OUTPUT_DIR = "output";

    public static void main(String[] args) throws Exception {
        new PuzzleBookGenerator().generateBook();
    }

    public void generateBook() throws Exception {
        List<Phrase> selectedPhrases = selectPhrases();
        List<Puzzle> puzzles = generatePuzzles(selectedPhrases);
        List<String> imagePaths = generatePuzzleImages(puzzles);
        XWPFDocument doc = new PuzzleBookDocumentWriter().createDocument(puzzles, imagePaths);
        saveDocument(doc);
    }

    private List<Phrase> selectPhrases() throws IOException {
        PhraseReader phraseReader = new PhraseReader();
        List<Phrase> allPhrases = phraseReader.readPhrasesFromExcel(
            new File("src/main/resources/" + PHRASES_FILE).getAbsolutePath()
        );
        if (allPhrases.size() < PUZZLE_COUNT) {
            throw new IllegalArgumentException("Not enough unique phrases in the source file.");
        }
        return allPhrases.stream().distinct().limit(PUZZLE_COUNT).collect(Collectors.toList());
    }

    private List<Puzzle> generatePuzzles(List<Phrase> phrases) {
        PuzzleGenerator generator = new PuzzleGenerator();
        List<Puzzle> puzzles = new ArrayList<>();
        for (Phrase phrase : phrases) {
            puzzles.add(generator.buildPuzzle(phrase));
        }
        return puzzles;
    }

    private List<String> generatePuzzleImages(List<Puzzle> puzzles) {
        File imageDir = new File(OUTPUT_DIR, "images");
        imageDir.mkdirs();
        List<String> imagePaths = new ArrayList<>();
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            String imageName = "puzzle-" + (i + 1) + ".png";
            String imagePath = new File(imageDir, imageName).getAbsolutePath();
            new PuzzleImageWriter(puzzle).generate(imageDir.getAbsolutePath(), imageName, false);
            imagePaths.add(imagePath);
        }
        return imagePaths;
    }

    private void saveDocument(XWPFDocument doc) throws IOException {
        new File(OUTPUT_DIR).mkdirs();
        try (FileOutputStream out = new FileOutputStream(new File(OUTPUT_DIR, OUTPUT_DOCX))) {
            doc.write(out);
        }
        doc.close();
        System.out.println("Puzzle book generated: " + new File(OUTPUT_DIR, OUTPUT_DOCX).getAbsolutePath());
    }
}
