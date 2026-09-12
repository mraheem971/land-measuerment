package com.land.measurement.model;

public enum MarlaStandard {
    STD_272_25(272.25, "272.25 Sq Ft (Patwari Standard)", 30.25),
    STD_225(225.0, "225 Sq Ft (Punjab / Housing Schemes)", 25.0),
    STD_250(250.0, "250 Sq Ft (Custom)", 27.778);

    private final double sqFtPerMarla;
    private final String displayName;
    private final double sqFtPerSarsahi;

    MarlaStandard(double sqFtPerMarla, String displayName, double sqFtPerSarsahi) {
        this.sqFtPerMarla = sqFtPerMarla;
        this.displayName = displayName;
        this.sqFtPerSarsahi = sqFtPerSarsahi;
    }

    public double getSqFtPerMarla() {
        return sqFtPerMarla;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getSqFtPerSarsahi() {
        return sqFtPerSarsahi;
    }
}
