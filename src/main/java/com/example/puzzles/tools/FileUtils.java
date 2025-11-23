package com.example.puzzles.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtils {

    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    public static void writeToFile(String content, String outputDir, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputDir + File.separator + fileName))) {
            writer.write(content);
        } catch (IOException e) {
            logger.error("Error writing file: " + fileName + " - " + e.getMessage(), e);
            throw e;
        }
    }
}
