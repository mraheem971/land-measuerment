package com.land.measurement.model;

import java.io.Serializable;
import java.util.Locale;

public class LandCalculationResult implements Serializable {
    private final double totalSqFeet;
    private final double totalSqYards;
    private final double totalSqMeters;
    private final double totalMarlas;
    private final double totalKanals;
    private final double totalAcres;
    private final double totalMurabbas;

    // Breakdown components
    private final int kanalsBreakdown;
    private final int marlasBreakdown;
    private final int sarsahiBreakdown;
    private final double sqFtRemaining;

    private final MarlaStandard marlaStandard;
    private final CalculationMode mode;
    private final double sideA;
    private final double sideB;
    private final double sideC;
    private final double sideD;
    private final double diagonal;
    private final InputUnit inputUnit;

    public LandCalculationResult(double totalSqFeet, MarlaStandard marlaStandard,
                                 CalculationMode mode, InputUnit inputUnit,
                                 double sideA, double sideB, double sideC, double sideD, double diagonal) {
        this.totalSqFeet = totalSqFeet;
        this.marlaStandard = marlaStandard;
        this.mode = mode;
        this.inputUnit = inputUnit;
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
        this.sideD = sideD;
        this.diagonal = diagonal;

        this.totalSqYards = totalSqFeet / 9.0;
        this.totalSqMeters = totalSqFeet / 10.7639104;

        double sqFtPerMarla = marlaStandard.getSqFtPerMarla();
        this.totalMarlas = totalSqFeet / sqFtPerMarla;
        this.totalKanals = totalMarlas / 20.0;
        this.totalAcres = totalKanals / 8.0;
        this.totalMurabbas = totalAcres / 25.0;

        // Breakdown calculation: Kanals -> Marlas -> Sarsahi -> Sq Ft
        double remaining = totalSqFeet;
        double sqFtPerKanal = sqFtPerMarla * 20.0;
        this.kanalsBreakdown = (int) (remaining / sqFtPerKanal);
        remaining -= kanalsBreakdown * sqFtPerKanal;

        this.marlasBreakdown = (int) (remaining / sqFtPerMarla);
        remaining -= marlasBreakdown * sqFtPerMarla;

        double sqFtPerSarsahi = marlaStandard.getSqFtPerSarsahi();
        this.sarsahiBreakdown = (int) (remaining / sqFtPerSarsahi);
        remaining -= sarsahiBreakdown * sqFtPerSarsahi;

        this.sqFtRemaining = Math.max(0.0, remaining);
    }

    public double getTotalSqFeet() {
        return totalSqFeet;
    }

    public double getTotalSqYards() {
        return totalSqYards;
    }

    public double getTotalSqMeters() {
        return totalSqMeters;
    }

    public double getTotalMarlas() {
        return totalMarlas;
    }

    public double getTotalKanals() {
        return totalKanals;
    }

    public double getTotalAcres() {
        return totalAcres;
    }

    public double getTotalMurabbas() {
        return totalMurabbas;
    }

    public int getKanalsBreakdown() {
        return kanalsBreakdown;
    }

    public int getMarlasBreakdown() {
        return marlasBreakdown;
    }

    public int getSarsahiBreakdown() {
        return sarsahiBreakdown;
    }

    public double getSqFtRemaining() {
        return sqFtRemaining;
    }

    public MarlaStandard getMarlaStandard() {
        return marlaStandard;
    }

    public CalculationMode getMode() {
        return mode;
    }

    public InputUnit getInputUnit() {
        return inputUnit;
    }

    public double getSideA() {
        return sideA;
    }

    public double getSideB() {
        return sideB;
    }

    public double getSideC() {
        return sideC;
    }

    public double getSideD() {
        return sideD;
    }

    public double getDiagonal() {
        return diagonal;
    }

    public String getFormattedBreakdown() {
        StringBuilder sb = new StringBuilder();
        if (kanalsBreakdown > 0) {
            sb.append(kanalsBreakdown).append(kanalsBreakdown == 1 ? " Kanal, " : " Kanals, ");
        }
        if (marlasBreakdown > 0 || kanalsBreakdown > 0) {
            sb.append(marlasBreakdown).append(marlasBreakdown == 1 ? " Marla" : " Marlas");
        } else if (kanalsBreakdown == 0 && marlasBreakdown == 0 && sarsahiBreakdown == 0 && sqFtRemaining < marlaStandard.getSqFtPerMarla()) {
            sb.append(String.format(Locale.US, "%.2f Marla", totalMarlas));
        }
        if (sarsahiBreakdown > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(sarsahiBreakdown).append(" Sarsahi");
        }
        if (sqFtRemaining > 0.05) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(String.format(Locale.US, "%.1f Sq Ft", sqFtRemaining));
        }
        String res = sb.toString().trim();
        return res.isEmpty() ? "0 Sq Ft" : res;
    }
}
