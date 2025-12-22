package org.example.entity;

public enum Language {
    ENGLISH("英語"),
    JAPANESE("日語"),
    KOREAN("韓語"),
    FRENCH("法語"),
    SPANISH("西班牙語"),
    PORTUGUESE("葡萄牙語"),
    GERMAN("德語"),
    ITALIAN("義大利語"),
    RUSSIAN("俄羅斯語"),
    TURKISH("土耳其語"),
    ARABIC("阿拉伯語"),
    THAI("泰語"),
    VIETNAMESE("越南語"),
    INDONESIAN("印尼語");

    private final String chineseName;

    Language(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getChineseName() {
        return chineseName;
    }
}
