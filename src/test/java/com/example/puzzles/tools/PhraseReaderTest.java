package com.example.puzzles.tools;

import com.example.puzzles.model.Phrase;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PhraseReaderTest {


    @Test
    public void testReadPhrasesFromExcel() {
        URL resource = getClass().getClassLoader().getResource("phrases/Iconic_Book_Quotes.xlsx");
        assertNotNull(resource, "Test file not found: Iconic_Book_Quotes.xlsx");

        PhraseReader phraseReader = new PhraseReader();
        List<Phrase> phrases = null;
        try {
            phrases = phraseReader.readPhrasesFromExcel(resource.getPath());
        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }

        assertNotNull(phrases, "Phrases list should not be null");
        assertFalse(phrases.isEmpty(), "Phrases list should not be empty");
    }

}
