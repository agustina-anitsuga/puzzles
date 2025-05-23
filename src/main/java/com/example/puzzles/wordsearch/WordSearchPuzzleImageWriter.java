package com.example.puzzles.wordsearch;

import javax.imageio.ImageIO;

import com.example.puzzles.model.Coordinate;
import com.example.puzzles.model.Direction;
import com.example.puzzles.model.Position;
import com.example.puzzles.model.Word;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class WordSearchPuzzleImageWriter {

    private final char[][] grid;
    private final int gridSize;
    private final int cellSize = 32;
    private final int padding = 20;
    private final String fontName = "Monospaced";
    private final boolean solution;
    private final List<Word> words;

    public WordSearchPuzzleImageWriter(char[][] grid, int gridSize, boolean solution, List<Word> words) {
        this.grid = grid;
        this.gridSize = gridSize;
        this.solution = solution;
        this.words = words;
    }

    public void writeToFile(String filePath) throws IOException {
        int imgSize = gridSize * cellSize + 2 * padding;
        BufferedImage image = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, imgSize, imgSize);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(fontName, Font.BOLD, cellSize - 6));

        // Draw grid and letters
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int x = padding + j * cellSize;
                int y = padding + i * cellSize;
                g2d.drawRect(x, y, cellSize, cellSize);
                String letter = String.valueOf(grid[i][j]);
                FontMetrics metrics = g2d.getFontMetrics();
                int textX = x + (cellSize - metrics.stringWidth(letter)) / 2;
                int textY = y + ((cellSize - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(letter, textX, textY);
            }
        }

        // If solution, highlight the words
        if (solution && words != null) {
            g2d.setColor(new Color(255, 255, 0, 128)); // semi-transparent yellow
            for (Word word : words) {
                highlightWord(g2d,word);
            }
        }
        g2d.dispose();
        ImageIO.write(image, "png", new File(filePath));
    }

    // Highlight the word in the grid
    private void highlightWord(Graphics2D g2d, Word word) {
        if (word == null || word.getPosition() == null) return;
        Position pos = word.getPosition();
        Coordinate coord = pos.getCoordinate();
        Direction dir = pos.getDirection();
        int row = coord.getRow();
        int col = coord.getCol();
        int len = word.getWord().length();
        for (int k = 0; k < len; k++) {
            int x = padding + (col + k * dir.dCol) * cellSize;
            int y = padding + (row + k * dir.dRow) * cellSize;
            g2d.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
        }
    }
}
