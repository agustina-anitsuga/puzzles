package com.example.puzzles.acrostics;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.tools.PhraseReader;
import com.example.puzzles.tools.PuzzleProperties;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AcrosticPuzzleBookGenerator {

    private static final int PUZZLE_COUNT = PuzzleProperties.getIntProperty("acrostic.book.puzzle.phrase.count");
    private static final int MIN_PHRASE_LENGTH = PuzzleProperties.getIntProperty("acrostic.book.puzzle.phrase.length.min");
    private static final int MAX_PHRASE_LENGTH = PuzzleProperties.getIntProperty("acrostic.book.puzzle.phrase.length.max");
    private static final int PHRASE_LONG_CAP = PuzzleProperties.getIntProperty("acrostic.book.puzzle.phrase.long");

    public static void main(String[] args) throws Exception {
        new AcrosticPuzzleBookGenerator().generateBook();
    }

    public void generateBook() throws Exception {
        List<Phrase> selectedPhrases = selectPhrases();
        List<Puzzle> puzzles = generatePuzzles(selectedPhrases);
        List<String> imagePaths = generatePuzzleImages(puzzles);
        List<String> clueImagePaths = generatePuzzleCluesImages(puzzles);
        XWPFDocument doc = new AcrosticPuzzleBookDocumentWriter().createDocument(puzzles, imagePaths, clueImagePaths);
        saveDocument(doc);
    }

    private List<String> generatePuzzleCluesImages(List<Puzzle> puzzles) {
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");
        File imageDir = new File(outputDir);
        imageDir.mkdirs();
        List<String> imagePaths = new ArrayList<>();
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            String imageName = "puzzle-" + (i + 1) + "-character-clues.png";
            String imagePath = new File(imageDir, imageName).getAbsolutePath();
            new AcrosticCharacterCluesImageWriter(puzzle).generate(imageDir.getAbsolutePath(), imageName);
            imagePaths.add(imagePath);
        }
        return imagePaths;
    }

    private List<Phrase> selectPhrases() throws IOException {
        PhraseReader phraseReader = new PhraseReader();
        String phrasesFile = PuzzleProperties.getProperty("phrases.file.path");
        List<Phrase> allPhrases = phraseReader.readPhrasesFromExcel(
            new File(phrasesFile).getAbsolutePath()
        );
        if (allPhrases.size() < PUZZLE_COUNT) {
            throw new IllegalArgumentException("Not enough unique phrases in the source file.");
        }
        return allPhrases.stream()
            .filter(p -> p.getCharactersInPhrase().length() >= MIN_PHRASE_LENGTH && p.getCharactersInPhrase().length() <= MAX_PHRASE_LENGTH)
            .peek(p -> p.setLongCap(PHRASE_LONG_CAP))
            .distinct()
            .limit(PUZZLE_COUNT)
            .collect(Collectors.toList());
    }

    private List<Puzzle> generatePuzzles(List<Phrase> phrases) {
        AcrosticPuzzleGenerator generator = new AcrosticPuzzleGenerator();
        List<Puzzle> puzzles = new ArrayList<>();
        for (Phrase phrase : phrases) {
            puzzles.add(generator.buildPuzzle(phrase));
        }
        return puzzles;
    }

    private List<String> generatePuzzleImages(List<Puzzle> puzzles) {
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");
        File imageDir = new File(outputDir);
        imageDir.mkdirs();
        List<String> imagePaths = new ArrayList<>();
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            String imageName = "puzzle-" + (i + 1) + ".png";
            String imagePath = new File(imageDir, imageName).getAbsolutePath();
            new AcrosticPuzzleImageWriter(puzzle).generate(imageDir.getAbsolutePath(), imageName, false);
            imagePaths.add(imagePath);
        }
        return imagePaths;
    }

    private void saveDocument(XWPFDocument doc) throws IOException {
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");
        String outputDocx = PuzzleProperties.getProperty("output.acrostic.book.docx");
        new File(outputDir).mkdirs();
        try (FileOutputStream out = new FileOutputStream(new File(outputDir, outputDocx))) {
            doc.write(out);
        }
        doc.close();
        System.out.println("Puzzle book generated: " + new File(outputDir, outputDocx).getAbsolutePath());
    }
}
