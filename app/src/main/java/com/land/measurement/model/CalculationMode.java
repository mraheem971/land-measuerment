package com.land.measurement.model;

public enum CalculationMode {
    EQUAL_SIDES("Equal / Regular 4 Sides (Average)"),
    HERON_IRREGULAR("Irregular 4 Sides + Diagonal (Heron's Formula)");

    private final String englishName;

    CalculationMode(String englishName) {
        this.englishName = englishName;
    }

    public String getEnglishName() {
        return englishName;
    }
}
