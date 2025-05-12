package com.example.puzzles;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PuzzleImage {

    private Puzzle puzzle;

    private int gridSize;
    private int cellSize;
    private int imageSize;

    private static final Logger logger = LogManager.getLogger(PuzzleImage.class);

    public PuzzleImage( Puzzle puzzle ){
        this.puzzle = puzzle;

        gridSize = Math.max(
                puzzle.getPhraseLength(), puzzle.getWords().size());
        cellSize = 50;
        imageSize = gridSize * cellSize;
    }

    private void drawSquare(Graphics2D g2d, int x, int y, int cellSize, boolean isIntersecting) {
        if (isIntersecting) {
            g2d.setColor(new Color(240, 240, 240)); // Grey background for intersection
        } else {
            g2d.setColor(Color.WHITE); // White background for non-intersecting letters
        }
        g2d.fillRect(x, y, cellSize, cellSize);
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(x, y, cellSize, cellSize);
    }

    private void drawLetter(Graphics2D g2d, char letter, int x, int y, int cellSize) {
        g2d.setColor(Color.BLACK);
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (cellSize - metrics.stringWidth(String.valueOf(letter))) / 2;
        int textY = y + ((cellSize - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(String.valueOf(letter), textX, textY);
    }

    private void drawPhrase(Graphics2D g2d, String phrase, int centerColumn, int cellSize, boolean isSolution) {
        for (int i = 0; i < phrase.length(); i++) {
            char currentChar = phrase.charAt(i);
            int x = centerColumn * cellSize;
            int y = i * cellSize;
            drawSquare(g2d, x, y, cellSize, true);
            if(isSolution)
                drawLetter(g2d, currentChar, x, y, cellSize);
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

            for (int j = 0; j < word.length(); j++) {
                char wordChar = word.charAt(j);
                int x = (j - index + centerColumn) * cellSize;
                int y = i * cellSize;

                boolean isIntersecting = (j == index);
                drawSquare(g2d, x, y, cellSize, isIntersecting);
                if(isSolution)
                    drawLetter(g2d, wordChar, x, y, cellSize);
            }
        }
    }

    public void generate(String path, String fileName, boolean isSolution) {
        String phrase = this.puzzle.getPhrase().getCharactersInPhrase();
        List<Word> words = this.puzzle.getWords();

        if (phrase.length() != words.size()) {
            logger.error("Phrase length does not match the number of words.");
            return;
        }

        BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageSize, imageSize);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));

        int centerColumn = gridSize / 2;
        drawPhrase(g2d, phrase, centerColumn, cellSize, isSolution);
        drawWords(g2d, phrase, words, centerColumn, cellSize, isSolution);

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
