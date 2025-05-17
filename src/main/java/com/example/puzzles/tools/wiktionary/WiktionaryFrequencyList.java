package com.example.puzzles.tools.wiktionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WiktionaryFrequencyList {

    private static final Logger logger = LogManager.getLogger(WiktionaryFrequencyList.class);

    public static void processFrequencyFile(String inputFile, String outputFilePath) throws IOException {
        
        File outputDir = new File(outputFilePath);
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                throw new IOException("Failed to create output directory: " + outputDir.getAbsolutePath());
            }
        }
        
        Pattern pattern = Pattern.compile("title=\\\"(.*?)\\\"");

        InputStream inputStream = WiktionaryFrequencyList.class.getClassLoader().getResourceAsStream(inputFile);
        if (inputStream == null) {
            throw new IOException("Resource not found: words/wiktionary-frequency.txt");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String title = matcher.group(1);
                    if (title.length() >= 5 && !title.contains(" ")) {
                        writer.write(title);
                        writer.newLine();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            String inputFile = getProperty("wiktionary.input.file");
            String outputFilePath = getProperty("words.file.path");
            processFrequencyFile(inputFile, outputFilePath);
            logger.info("Frequency file processed successfully.");
        } catch (IOException e) {
            logger.error("Error processing frequency file: " + e.getMessage(), e);
        }
    }

    private static String getProperty(String key) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = WiktionaryFrequencyList.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            properties.load(input);
        }
        return properties.getProperty(key);
    }
}
