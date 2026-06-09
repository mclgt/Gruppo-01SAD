package com.Model;

public enum TrackTag {
    NONE(""),
    FAVOURITE("Preferita"),
    EXPLICIT("Esplicita"),
    NEW_RELEASE("Novità");

    private final String tagName;

    TrackTag(String tagName) {
        this.tagName = tagName;
    }
    public String getTagName() {
        return tagName;
    }

}
