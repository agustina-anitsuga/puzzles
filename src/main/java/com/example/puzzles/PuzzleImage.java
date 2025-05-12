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

    public void generate( String path, String fileName) {

        String phrase = this.puzzle.getPhrase().getCharactersInPhrase();
        List<Word> words = this.puzzle.getWords();

        if(phrase.length() != words.size()) {
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
        for (int i = 0; i < phrase.length(); i++) {
            char currentChar = phrase.charAt(i);
            int x = centerColumn * cellSize;
            int y = i * cellSize;
            g2d.setColor(new Color(240, 240, 240)); // Very light grey background
            g2d.fillRect(x, y, cellSize, cellSize);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(x, y, cellSize, cellSize);
            g2d.setColor(Color.BLACK);
            FontMetrics metrics = g2d.getFontMetrics();
            int textX = x + (cellSize - metrics.stringWidth(String.valueOf(currentChar))) / 2;
            int textY = y + ((cellSize - metrics.getHeight()) / 2) + metrics.getAscent();
            g2d.drawString(String.valueOf(currentChar), textX, textY);
        }

        for (int i = 0; i < words.size(); i++) {
            Character phraseCharacter = phrase.charAt(i);

            String word = words.get(i).getWord().toLowerCase();
            for (int j = 0; j < word.length(); j++) {
                char wordChar = word.charAt(j);

                phraseCharacter = Character.toLowerCase(phraseCharacter);

                int x = j * cellSize;
                int y = i * cellSize;

                // Check if the phrase intersects with the current word
                if (j < phrase.length() && phrase.charAt(j) == wordChar) {
                    g2d.setColor(new Color(240, 240, 240)); // Grey background for intersection
                    g2d.fillRect(x, y, cellSize, cellSize);
                } else {
                    g2d.setColor(Color.WHITE); // White background for non-intersecting letters
                    g2d.fillRect(x, y, cellSize, cellSize);
                }

                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawRect(x, y, cellSize, cellSize);
                g2d.setColor(Color.BLACK);
                FontMetrics metrics = g2d.getFontMetrics();
                int textX = x + (cellSize - metrics.stringWidth(String.valueOf(wordChar))) / 2;
                int textY = y + ((cellSize - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(String.valueOf(wordChar), textX, textY);
            }
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
            logger.error("Error saving puzzle image: " + e.getMessage());
        }
    }
}
