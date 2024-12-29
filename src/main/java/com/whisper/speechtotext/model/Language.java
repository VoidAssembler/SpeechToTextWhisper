package com.whisper.speechtotext.model;

public enum Language {
    AUTO("auto"),
    ENGLISH("en"),
    RUSSIAN("ru"),
    SPANISH("es"),
    FRENCH("fr"),
    GERMAN("de"),
    ITALIAN("it"),
    PORTUGUESE("pt"),
    DUTCH("nl"),
    POLISH("pl"),
    TURKISH("tr"),
    CHINESE("zh");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Language fromString(String code) {
        if (code == null) return AUTO; // default value
        
        for (Language language : Language.values()) {
            if (language.code.equalsIgnoreCase(code)) {
                return language;
            }
        }
        return AUTO;
    }
}
