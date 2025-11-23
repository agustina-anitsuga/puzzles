package com.example.puzzles.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AcrosticPuzzlePosition.class, name = "acrostic"),
        @JsonSubTypes.Type(value = WordSearchPosition.class, name = "wordsearch")
})
public interface Position {
 
}
