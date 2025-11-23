package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.AcrosticPuzzleBook;
import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.tools.FileUtils;
import com.example.puzzles.tools.PhraseReader;
import com.example.puzzles.tools.PuzzleProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
        String name = "acrostic-puzzle-book.json";
        //new AcrosticPuzzleBookGenerator().generateBook(name);
        new AcrosticPuzzleBookGenerator().regenerateBook(name);
    }

    public AcrosticPuzzleBook regenerateBook(String name) throws Exception {
        AcrosticPuzzleBook book = readBook(name);
        book.getPuzzles().stream().forEach( puzzle -> { try {
            new AcrosticPuzzleGenerator().generatePuzzleFiles(puzzle);
        } catch (Exception e) {
            e.printStackTrace();
        } } );
        generateWordDocument(book);
        return book;
    }

    private AcrosticPuzzleBook readBook(String name) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");
        AcrosticPuzzleBook book = mapper.readValue(new File(outputDir+"/"+name), AcrosticPuzzleBook.class);
        return book;
    }

    public AcrosticPuzzleBook generateBook(String name) throws Exception {
        List<Phrase> selectedPhrases = selectPhrases();
        List<AcrosticPuzzle> puzzles = generatePuzzles(selectedPhrases);
        List<String> imagePaths = generatePuzzleImages(puzzles);
        List<String> clueImagePaths = generatePuzzleCluesImages(puzzles);
        AcrosticPuzzleBook book = new AcrosticPuzzleBook(name,selectedPhrases,puzzles,imagePaths,clueImagePaths);
        generateJson(book);
        generateWordDocument(book);
        return book;
    }

    private void generateJson(AcrosticPuzzleBook book) throws IOException {
        StringBuffer sb = new StringBuffer();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        sb.append( mapper.writeValueAsString(book) );
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");
        FileUtils.writeToFile(sb.toString(), outputDir, book.getName());
    }

    private void generateWordDocument(AcrosticPuzzleBook book)
            throws Exception, IOException {
        XWPFDocument doc = new AcrosticPuzzleBookDocumentWriter().createDocument(book.getPuzzles(), book.getImagePaths(), book.getClueImagePaths());
        saveDocument(doc);
    }

    private List<String> generatePuzzleCluesImages(List<AcrosticPuzzle> puzzles) {
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");
        File imageDir = new File(outputDir);
        imageDir.mkdirs();
        List<String> imagePaths = new ArrayList<>();
        for (int i = 0; i < puzzles.size(); i++) {
            Puzzle puzzle = puzzles.get(i);
            String imageName = puzzle.getName() + "-character-clue.png";
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

    private List<AcrosticPuzzle> generatePuzzles(List<Phrase> phrases) {
        AcrosticPuzzleGenerator generator = new AcrosticPuzzleGenerator();
        List<AcrosticPuzzle> puzzles = new ArrayList<>();
        for (Phrase phrase : phrases) {
            puzzles.add(generator.buildPuzzle(phrase));
        }
        return puzzles;
    }

    private List<String> generatePuzzleImages(List<AcrosticPuzzle> puzzles) {
        String outputDir = PuzzleProperties.getProperty("puzzles.output.dir");
        File imageDir = new File(outputDir);
        imageDir.mkdirs();
        List<String> imagePaths = new ArrayList<>();
        for (int i = 0; i < puzzles.size(); i++) {
            AcrosticPuzzle puzzle = puzzles.get(i);
            // generate blank puzzle image
            String imageName = puzzle.getName() + ".png";
            String imagePath = new File(imageDir, imageName).getAbsolutePath();
            AcrosticPuzzleImageWriter iw = new AcrosticPuzzleImageWriter(puzzle);
            iw.generate(imageDir.getAbsolutePath(), imageName, false);
            imagePaths.add(imagePath);
            // generate solution puzzle image
            imageName = puzzle.getName() + "-sol.png";
            iw.generate(imageDir.getAbsolutePath(), imageName, true);
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
