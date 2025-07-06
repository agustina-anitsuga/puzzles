package com.example.puzzles.model;

import java.util.List;

public class AcrosticPuzzlePosition implements Position {
    
    private List<Integer> intersections;
    private List<Integer> intersectingChunk;

    public AcrosticPuzzlePosition(Integer intersection) {
        this.intersections = List.of(intersection);
        this.intersectingChunk = List.of(0);
    }

    public AcrosticPuzzlePosition(List<Integer> intersections, List<Integer> intersectingChunk) {
        this.intersections = intersections;
        this.intersectingChunk = intersectingChunk;
    }

    public List<Integer> getIntersections() {
        return intersections;
    }

    public List<Integer> getIntersectingChunk() {
        return intersectingChunk;
    }

}
