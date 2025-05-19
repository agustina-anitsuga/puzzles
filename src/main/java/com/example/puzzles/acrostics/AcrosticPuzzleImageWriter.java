package com.example.puzzles.acrostics;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import com.example.puzzles.model.Phrase;
import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AcrosticPuzzleImageWriter {

    private static final int GRID_SIZE = 20;

    private Puzzle puzzle;

    private int gridHeight;
    private int gridWidth;
    private int cellSize;
    private int imageWidth;
    private int imageHeight;
    private int maxToLeft = 0;
    private int maxToRight = 0;

    private static final Logger logger = LogManager.getLogger(AcrosticPuzzleImageWriter.class);

    public AcrosticPuzzleImageWriter( Puzzle puzzle ){
        this.puzzle = puzzle;
        setSizes(puzzle);
    }

    private void setSizes(Puzzle aPuzzle) {
        gridHeight = puzzle.getPhrase().getChunks().getFirst().length() + 1;
        cellSize = GRID_SIZE;

        int wordNum = 0;
        for (Word word : aPuzzle.getWords()) {
            char phraseChar = aPuzzle.getPhrase().getChunks().getFirst().charAt(wordNum++);
            int index = word.getWord().indexOf(phraseChar);
            if( index > maxToLeft ){
                maxToLeft = index;
            }
            if( word.getWord().length() - index > maxToRight ){
                maxToRight = word.getWord().length() - index;
            }
        }

        gridWidth = maxToLeft + maxToRight + 1 + puzzle.getPhrase().getDistanceBetweenChunks();

        imageWidth = gridWidth * cellSize;
        imageHeight = gridHeight * cellSize;
    }

    private void drawSquare(Graphics2D g2d, int x, int y, int cellSize, boolean isIntersecting) {
        if (isIntersecting) {
            g2d.setColor(new Color(240,240,240)); // Grey background for intersection
        } else {
            g2d.setColor(Color.WHITE); // White background for non-intersecting letters
        }
        g2d.fillRect(x, y, cellSize, cellSize);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(x, y, cellSize, cellSize);
    }

    private void drawLetter(Graphics2D g2d, String letter, int x, int y, int cellSize) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, (int)(cellSize * 0.75) ));
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (cellSize - metrics.stringWidth(String.valueOf(letter))) / 3;
        int textY = y + ((cellSize - metrics.getHeight()) / 3) + metrics.getAscent();
        g2d.drawString(String.valueOf(letter), textX, textY);
    }

    private void drawPhrase(Graphics2D g2d, String phrase, int centerColumn, int cellSize, boolean isSolution) {
        for (int i = 0; i < phrase.length(); i++) {
            char currentChar = phrase.charAt(i);
            int x = centerColumn * cellSize;
            int y = i * cellSize;
            drawSquare(g2d, x, y, cellSize, true);
            if(isSolution)
                drawLetter(g2d, String.valueOf(currentChar), x, y, cellSize);
        }
    }

    private void drawWords(Graphics2D g2d, String phrase, List<Word> words, int centerColumn, int cellSize, boolean isSolution) {
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i).getWord().toLowerCase();

            int index = word.indexOf(phrase.charAt(i));
            if (index == -1) {
                logger.error("Word does not contain the character from the phrase: " + word);
                continue;
            }

            // Draw the sequence number
            g2d.setColor(Color.BLACK);
            int numberX = (centerColumn - index - 1) * cellSize; // Adjust position for the number
            int numberY = i * cellSize + cellSize / 3; // Adjust position for the number
            drawLetter(g2d, String.valueOf(i + 1), numberX, numberY, cellSize/2);

            for (int j = 0; j < word.length(); j++) {
                char wordChar = word.charAt(j);
                int x = (j - index + centerColumn) * cellSize;
                int y = i * cellSize;

                boolean isIntersecting = (j == index);
                drawSquare(g2d, x, y, cellSize, isIntersecting);
                if(isSolution)
                    drawLetter(g2d, String.valueOf(wordChar), x, y, cellSize);
            }
        }
    }

    public void generate(String path, String fileName, boolean isSolution) {
        Phrase phrase = this.puzzle.getPhrase();
        List<Word> words = this.puzzle.getWords();

        if (phrase.getChunks().getFirst().length() != words.size()) {
            logger.error("Phrase length does not match the number of words.");
            return;
        }

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageWidth, imageHeight);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));

        int centerColumn = (maxToLeft+1) ;
        drawWords(g2d, phrase.getChunks().getFirst(), words, centerColumn, cellSize, isSolution);
        drawPhrase(g2d, phrase.getChunks().getFirst(), centerColumn, cellSize, isSolution);
        if(phrase.chunkCount() > 1) {
            drawPhrase(g2d, phrase.getChunks().getLast(), 
                centerColumn + phrase.getDistanceBetweenChunks() + 1, cellSize, isSolution);
        }

        g2d.dispose();

        File outputDir = new File(path);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir, fileName);
        try {
            ImageIO.write(image, "png", outputFile);
            logger.info("Puzzle image generated at: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Error saving puzzle image: " + e.getMessage(), e);
        }
    }
}
