package com.land.measurement.model;

import java.io.Serializable;

public class LandRecord implements Serializable {
    private long id;
    private String title;
    private String location;
    private double totalSqFeet;
    private double totalMarlas;
    private double totalKanals;
    private String breakdownSummary;
    private String calculationMode;
    private String marlaStandard;
    private String inputUnit;
    private double sideA;
    private double sideB;
    private double sideC;
    private double sideD;
    private double diagonal;
    private long timestamp;

    public LandRecord() {
    }

    public LandRecord(long id, String title, String location, double totalSqFeet,
                      double totalMarlas, double totalKanals, String breakdownSummary,
                      String calculationMode, String marlaStandard, String inputUnit,
                      double sideA, double sideB, double sideC, double sideD,
                      double diagonal, long timestamp) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.totalSqFeet = totalSqFeet;
        this.totalMarlas = totalMarlas;
        this.totalKanals = totalKanals;
        this.breakdownSummary = breakdownSummary;
        this.calculationMode = calculationMode;
        this.marlaStandard = marlaStandard;
        this.inputUnit = inputUnit;
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
        this.sideD = sideD;
        this.diagonal = diagonal;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTotalSqFeet() {
        return totalSqFeet;
    }

    public void setTotalSqFeet(double totalSqFeet) {
        this.totalSqFeet = totalSqFeet;
    }

    public double getTotalMarlas() {
        return totalMarlas;
    }

    public void setTotalMarlas(double totalMarlas) {
        this.totalMarlas = totalMarlas;
    }

    public double getTotalKanals() {
        return totalKanals;
    }

    public void setTotalKanals(double totalKanals) {
        this.totalKanals = totalKanals;
    }

    public String getBreakdownSummary() {
        return breakdownSummary;
    }

    public void setBreakdownSummary(String breakdownSummary) {
        this.breakdownSummary = breakdownSummary;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public String getMarlaStandard() {
        return marlaStandard;
    }

    public void setMarlaStandard(String marlaStandard) {
        this.marlaStandard = marlaStandard;
    }

    public String getInputUnit() {
        return inputUnit;
    }

    public void setInputUnit(String inputUnit) {
        this.inputUnit = inputUnit;
    }

    public double getSideA() {
        return sideA;
    }

    public void setSideA(double sideA) {
        this.sideA = sideA;
    }

    public double getSideB() {
        return sideB;
    }

    public void setSideB(double sideB) {
        this.sideB = sideB;
    }

    public double getSideC() {
        return sideC;
    }

    public void setSideC(double sideC) {
        this.sideC = sideC;
    }

    public double getSideD() {
        return sideD;
    }

    public void setSideD(double sideD) {
        this.sideD = sideD;
    }

    public double getDiagonal() {
        return diagonal;
    }

    public void setDiagonal(double diagonal) {
        this.diagonal = diagonal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
