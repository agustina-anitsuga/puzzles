package com.example.puzzles.acrostics;

import com.example.puzzles.model.Puzzle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class AcrosticCharacterCluesImageWriter {

    private final Puzzle puzzle;
    private final int cellSize = 20;
    private final int padding = 3;
    private final int fontSize = 10;

    public AcrosticCharacterCluesImageWriter(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public void generate(String outputDir, String fileName) {
        List<Character> chars = getSortedCharacters();
        int gridSize = (int) Math.ceil(Math.sqrt(chars.size()));
        int width = gridSize * cellSize + 2 * padding;
        int height = gridSize * cellSize + 2 * padding;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));

        for (int i = 0; i < chars.size(); i++) {
            int row = i / gridSize;
            int col = i % gridSize;
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
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Character> getSortedCharacters() {
        List<Character> chars = new ArrayList<>();
        puzzle.getWords().stream()
            .flatMap(word -> word.getWord().chars().mapToObj(c -> (char) c))
            .sorted()
            .forEach(chars::add);
        return chars;
    }
}
