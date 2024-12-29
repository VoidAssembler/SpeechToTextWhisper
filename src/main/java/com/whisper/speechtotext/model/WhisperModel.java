package com.whisper.speechtotext.model;

public enum WhisperModel {
    TINY("tiny"),
    BASE("base"),
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large");

    private final String value;

    WhisperModel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WhisperModel fromString(String value) {
        if (value == null) return BASE; // default value
        
        for (WhisperModel model : WhisperModel.values()) {
            if (model.value.equalsIgnoreCase(value)) {
                return model;
            }
        }
        return BASE;
    }
}
