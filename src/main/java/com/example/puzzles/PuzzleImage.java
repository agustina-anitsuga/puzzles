package com.example.puzzles;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import com.example.puzzles.model.Puzzle;
import com.example.puzzles.model.Word;

public class PuzzleImage {

    private Puzzle puzzle;

    public PuzzleImage( Puzzle puzzle ){
        this.puzzle = puzzle;
    }

    public void generate( String path, String fileName) {

        String phrase = this.puzzle.getPhrase();
        List<Word> words = this.puzzle.getWords();
        
        // Remove punctuation marks and spaces from the phrase
        String sanitizedPhrase = phrase.replaceAll("[\\p{Punct}\\s]", "");

        int gridSize = Math.max(phrase.length(), words.size());
        int cellSize = 50;
        int imageSize = gridSize * cellSize;

        BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imageSize, imageSize);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));

        int centerColumn = gridSize / 2;
        for (int i = 0; i < sanitizedPhrase.length(); i++) {
            char currentChar = sanitizedPhrase.charAt(i);
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
            String word = words.get(i).getWord();
            for (int j = 0; j < word.length(); j++) {
                g2d.drawString(String.valueOf(word.charAt(j)), j * cellSize + 15, i * cellSize + 35);
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
            System.out.println("Puzzle image generated at: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving puzzle image: " + e.getMessage());
        }
    }
}
