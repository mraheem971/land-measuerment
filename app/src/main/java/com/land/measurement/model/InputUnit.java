package com.land.measurement.model;

public enum InputUnit {
    FEET(1.0, "Feet (ft)", "ft"),
    YARDS_GAJ(3.0, "Gaj / Yards (yd)", "yd"),
    METERS(3.280839895, "Meters (m)", "m"),
    KARAM(5.5, "Karam (5.5 ft)", "karam");

    private final double toFeetFactor;
    private final String displayName;
    private final String shortSymbol;

    InputUnit(double toFeetFactor, String displayName, String shortSymbol) {
        this.toFeetFactor = toFeetFactor;
        this.displayName = displayName;
        this.shortSymbol = shortSymbol;
    }

    public double getToFeetFactor() {
        return toFeetFactor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortSymbol() {
        return shortSymbol;
    }

    public double toFeet(double value) {
        return value * toFeetFactor;
    }

    public double fromFeet(double feet) {
        return feet / toFeetFactor;
    }
}
