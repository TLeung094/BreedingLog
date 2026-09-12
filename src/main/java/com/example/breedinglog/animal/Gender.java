package com.example.breedinglog.animal;

public enum Gender {
    MALE("♂"),
    FEMALE("♀");

    private final String symbol;

    Gender(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public static Gender random() {
        return Math.random() < 0.5 ? MALE : FEMALE;
    }
}
