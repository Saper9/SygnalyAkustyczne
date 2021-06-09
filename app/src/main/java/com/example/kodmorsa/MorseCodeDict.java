package com.example.kodmorsa;

import java.util.HashMap;
import java.util.Map;

public class MorseCodeDict {
    Map<String, String> dictionary = new HashMap<>();

    public MorseCodeDict() {
        this.dictionary.put(".-", "A");
        this.dictionary.put("-...", "B");
        this.dictionary.put("-.-.", "C");
        this.dictionary.put("-..", "D");
        this.dictionary.put(".", "E");
        this.dictionary.put("..-.", "F");
        this.dictionary.put("--.", "G");
        this.dictionary.put("....", "H");
        this.dictionary.put("..", "I");
        this.dictionary.put(".---", "J");
        this.dictionary.put("-.-", "K");
        this.dictionary.put(".-..", "L");
        this.dictionary.put("--", "M");
        this.dictionary.put("-.", "N");
        this.dictionary.put("---", "O");
        this.dictionary.put(".--.", "P");
        this.dictionary.put("--.-", "Q");
        this.dictionary.put(".-.", "R");
        this.dictionary.put("...", "S");
        this.dictionary.put("-", "T");
        this.dictionary.put("..-", "U");
        this.dictionary.put("...-", "V");
        this.dictionary.put(".--", "W");
        this.dictionary.put("-..-", "X");
        this.dictionary.put("-.--", "Y");
        this.dictionary.put("--..", "Z");
        this.dictionary.put("-----", "0");
        this.dictionary.put(".----", "1");
        this.dictionary.put("..---", "2");
        this.dictionary.put("...--", "3");
        this.dictionary.put("....-", "4");
        this.dictionary.put(".....", "5");
        this.dictionary.put("-....", "6");
        this.dictionary.put("--...", "7");
        this.dictionary.put("---..", "8");
        this.dictionary.put("----.", "9");
        this.dictionary.put(".-.-.-", ".");
        this.dictionary.put("--..--", ",");
        this.dictionary.put("---...", ":");
        this.dictionary.put("..--..", "?");
        this.dictionary.put("-...-", "=");
        this.dictionary.put("-....-", "-");
        this.dictionary.put("-.--.", "(");
        this.dictionary.put("-.--.-", ")");
        this.dictionary.put(".-..-.", "\"");
        this.dictionary.put(".----.", "'");
        this.dictionary.put("-..-.", "/");
        this.dictionary.put(".--.-.", "@");
        this.dictionary.put("-.-.--", "!");
    }

    public String translate(String value) {
        if (this.dictionary.containsKey((Object) value)) {
            return this.dictionary.get(value);
        } else {
            return "NULL";
        }
    }
}
