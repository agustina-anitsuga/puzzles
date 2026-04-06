package com.example.puzzles.acrostics;

import com.example.puzzles.model.AcrosticPuzzle;
import com.example.puzzles.model.Puzzle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class AcrosticCharacterCluesImageWriter {

    private static final String FONT_FAMILY = "Arial";
    private static final String FORMAT = "png";

    private static final int GRID_WIDTH = 30;
    
    private final AcrosticPuzzle puzzle;
    private final int cellSize = 15;
    private final int padding = 2;
    private final int fontSize = 10;

    public AcrosticCharacterCluesImageWriter(Puzzle puzzle) {
        this.puzzle = (AcrosticPuzzle) puzzle;
    }

    public void generate(String outputDir, String fileName) {
        List<Character> chars = puzzle.getSortedCharacters();
        int gridWidth = (int) GRID_WIDTH;
        int gridHeight = (int) Math.ceil((double) chars.size() / gridWidth);
        int width = gridWidth * cellSize + 2 * padding;
        int height = gridHeight * cellSize + 2 * padding;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font(FONT_FAMILY, Font.PLAIN, fontSize));

        for (int i = 0; i < chars.size(); i++) {
            int row = i / gridWidth;
            int col = i % gridWidth;
            int x = padding + col * cellSize;
            int y = padding + row * cellSize;
            // Draw cell border
            g2d.drawRect(x, y, cellSize, cellSize);
            // Draw character centered
            String ch = String.valueOf(chars.get(i));
            FontMetrics metrics = g2d.getFontMetrics();
            int textX = x + (cellSize - metrics.stringWidth(ch)) / 2;
            int textY = y + ((cellSize - metrics.getHeight()) / 2) + metrics.getAscent();
            g2d.setColor(Color.BLACK);
            g2d.drawString(ch, textX, textY);
            g2d.setColor(Color.LIGHT_GRAY);
        }
        g2d.dispose();

        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, fileName);
        try {
            ImageIO.write(image, FORMAT, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
