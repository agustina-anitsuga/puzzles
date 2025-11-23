package com.example.puzzles.model;

import java.util.List;
import lombok.Data;

@Data
public class AcrosticPuzzleBook {
        
    public AcrosticPuzzleBook(){
    }

    public AcrosticPuzzleBook(String name,
            List<Phrase> selectedPhrases, 
            List<AcrosticPuzzle> puzzles, 
            List<String> imagePaths,
            List<String> clueImagePaths) {
        this.name = name;
        this.selectedPhrases = selectedPhrases;
        this.puzzles = puzzles;
        this.imagePaths = imagePaths;
        this.clueImagePaths = clueImagePaths;
    }

    private String name;
    private List<Phrase> selectedPhrases;
    private List<AcrosticPuzzle> puzzles;
    private List<String> imagePaths;
    private List<String> clueImagePaths;
    
}
