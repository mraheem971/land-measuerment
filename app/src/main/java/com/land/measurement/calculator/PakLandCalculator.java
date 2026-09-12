package com.land.measurement.calculator;

import com.land.measurement.model.CalculationMode;
import com.land.measurement.model.InputUnit;
import com.land.measurement.model.LandCalculationResult;
import com.land.measurement.model.MarlaStandard;

public class PakLandCalculator {

    /**
     * Calculates area using 4 sides with average / rectangular method.
     */
    public static LandCalculationResult calculateEqualSides(
            double length1, double length2,
            double width1, double width2,
            InputUnit unit,
            MarlaStandard marlaStandard) {

        if (length1 <= 0 || width1 <= 0) {
            throw new IllegalArgumentException("Side lengths must be greater than zero.");
        }
        if (length2 <= 0) length2 = length1;
        if (width2 <= 0) width2 = width1;

        // Convert input measurements to feet
        double l1Feet = unit.toFeet(length1);
        double l2Feet = unit.toFeet(length2);
        double w1Feet = unit.toFeet(width1);
        double w2Feet = unit.toFeet(width2);

        double avgLength = (l1Feet + l2Feet) / 2.0;
        double avgWidth = (w1Feet + w2Feet) / 2.0;

        double totalSqFeet = avgLength * avgWidth;

        return new LandCalculationResult(
                totalSqFeet,
                marlaStandard,
                CalculationMode.EQUAL_SIDES,
                unit,
                length1, width1, length2, width2, 0.0
        );
    }

    /**
     * Calculates area of an irregular 4-sided plot using Heron's formula and diagonal.
     * Side A, Side B, Side C, Side D in cyclic order, with diagonal splitting into (A, B, Diag) and (C, D, Diag).
     */
    public static LandCalculationResult calculateHeronIrregular(
            double sideA, double sideB, double sideC, double sideD, double diagonal,
            InputUnit unit,
            MarlaStandard marlaStandard) {

        if (sideA <= 0 || sideB <= 0 || sideC <= 0 || sideD <= 0 || diagonal <= 0) {
            throw new IllegalArgumentException("All 4 sides and the diagonal must be greater than zero.");
        }

        // Convert to feet
        double aFeet = unit.toFeet(sideA);
        double bFeet = unit.toFeet(sideB);
        double cFeet = unit.toFeet(sideC);
        double dFeet = unit.toFeet(sideD);
        double diagFeet = unit.toFeet(diagonal);

        // Validate Triangle 1 (A, B, Diag)
        validateTriangle(aFeet, bFeet, diagFeet, "Triangle 1 (Side A, Side B, Diagonal)");

        // Validate Triangle 2 (C, D, Diag)
        validateTriangle(cFeet, dFeet, diagFeet, "Triangle 2 (Side C, Side D, Diagonal)");

        double area1 = calculateTriangleHeronArea(aFeet, bFeet, diagFeet);
        double area2 = calculateTriangleHeronArea(cFeet, dFeet, diagFeet);

        double totalSqFeet = area1 + area2;

        return new LandCalculationResult(
                totalSqFeet,
                marlaStandard,
                CalculationMode.HERON_IRREGULAR,
                unit,
                sideA, sideB, sideC, sideD, diagonal
        );
    }

    /**
     * Computes area of a single triangle with Heron's formula.
     */
    public static double calculateTriangleHeronArea(double a, double b, double c) {
        double s = (a + b + c) / 2.0;
        double val = s * (s - a) * (s - b) * (s - c);
        if (val < 0) {
            return 0.0;
        }
        return Math.sqrt(val);
    }

    /**
     * Checks if sides form a valid geometric triangle.
     */
    public static void validateTriangle(double a, double b, double c, String triangleName) {
        double epsilon = 1e-6;
        if (a + b <= c + epsilon || a + c <= b + epsilon || b + c <= a + epsilon) {
            throw new IllegalArgumentException(String.format(
                    "Invalid geometry in %s: Sides (%.2f, %.2f) and Diagonal (%.2f) do not form a closed triangle. The sum of any two sides must exceed the third.",
                    triangleName, a, b, c
            ));
        }
    }

    /**
     * Quick direct unit converter
     */
    public static double convertUnits(double value, String fromUnit, String toUnit, MarlaStandard standard) {
        double sqFtPerMarla = standard.getSqFtPerMarla();
        double sqFt = 0;

        switch (fromUnit) {
            case "SQ_FEET":
                sqFt = value;
                break;
            case "SQ_YARDS":
                sqFt = value * 9.0;
                break;
            case "SQ_METERS":
                sqFt = value * 10.7639104;
                break;
            case "MARLA":
                sqFt = value * sqFtPerMarla;
                break;
            case "KANAL":
                sqFt = value * (sqFtPerMarla * 20.0);
                break;
            case "ACRE":
                sqFt = value * (sqFtPerMarla * 160.0);
                break;
            case "MURABBA":
                sqFt = value * (sqFtPerMarla * 160.0 * 25.0);
                break;
            case "SARSAHI":
                sqFt = value * standard.getSqFtPerSarsahi();
                break;
            default:
                sqFt = value;
        }

        switch (toUnit) {
            case "SQ_FEET":
                return sqFt;
            case "SQ_YARDS":
                return sqFt / 9.0;
            case "SQ_METERS":
                return sqFt / 10.7639104;
            case "MARLA":
                return sqFt / sqFtPerMarla;
            case "KANAL":
                return sqFt / (sqFtPerMarla * 20.0);
            case "ACRE":
                return sqFt / (sqFtPerMarla * 160.0);
            case "MURABBA":
                return sqFt / (sqFtPerMarla * 160.0 * 25.0);
            case "SARSAHI":
                return sqFt / standard.getSqFtPerSarsahi();
            default:
                return sqFt;
        }
    }
}
