package com.land.measurement;

import com.land.measurement.calculator.PakLandCalculator;
import com.land.measurement.model.InputUnit;
import com.land.measurement.model.LandCalculationResult;
import com.land.measurement.model.MarlaStandard;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class PakLandCalculatorTest {

    @Test
    public void testEqualSidesStandard225() {
        // Plot: 30 ft x 60 ft = 1,800 sq ft
        // In 225 system: 1,800 / 225 = 8.0 Marlas, 0 Kanals
        LandCalculationResult result = PakLandCalculator.calculateEqualSides(
                30.0, 30.0, 60.0, 60.0,
                InputUnit.FEET,
                MarlaStandard.STD_225
        );

        assertEquals(1800.0, result.getTotalSqFeet(), 0.001);
        assertEquals(8.0, result.getTotalMarlas(), 0.001);
        assertEquals(0.4, result.getTotalKanals(), 0.001);
        assertEquals(200.0, result.getTotalSqYards(), 0.001);
        assertEquals(8, result.getMarlasBreakdown());
        assertEquals(0, result.getKanalsBreakdown());
    }

    @Test
    public void testEqualSidesOneKanal() {
        // Plot: 50 ft x 90 ft = 4,500 sq ft
        // In 225 system: 4,500 sq ft = exactly 1 Kanal (20 Marlas)
        LandCalculationResult result = PakLandCalculator.calculateEqualSides(
                50.0, 50.0, 90.0, 90.0,
                InputUnit.FEET,
                MarlaStandard.STD_225
        );

        assertEquals(4500.0, result.getTotalSqFeet(), 0.001);
        assertEquals(20.0, result.getTotalMarlas(), 0.001);
        assertEquals(1.0, result.getTotalKanals(), 0.001);
        assertEquals(1, result.getKanalsBreakdown());
        assertEquals(0, result.getMarlasBreakdown());
    }

    @Test
    public void testEqualSidesGovtPatwari272() {
        // In 272.25 system: 1 Kanal = 20 * 272.25 = 5,445 sq ft
        LandCalculationResult result = PakLandCalculator.calculateEqualSides(
                54.45, 54.45, 100.0, 100.0,
                InputUnit.FEET,
                MarlaStandard.STD_272_25
        );

        assertEquals(5445.0, result.getTotalSqFeet(), 0.001);
        assertEquals(20.0, result.getTotalMarlas(), 0.001);
        assertEquals(1.0, result.getTotalKanals(), 0.001);
        assertEquals(1, result.getKanalsBreakdown());
    }

    @Test
    public void testHeronIrregularWith345Triangle() {
        // Quadrilateral with sides A=30, B=40, C=30, D=40, Diagonal=50
        // Triangle 1: (30, 40, 50) -> Area = (30 * 40)/2 = 600
        // Triangle 2: (30, 40, 50) -> Area = (30 * 40)/2 = 600
        // Total Area = 1200 sq ft
        LandCalculationResult result = PakLandCalculator.calculateHeronIrregular(
                30.0, 40.0, 30.0, 40.0, 50.0,
                InputUnit.FEET,
                MarlaStandard.STD_225
        );

        assertEquals(1200.0, result.getTotalSqFeet(), 0.01);
        assertEquals(1200.0 / 225.0, result.getTotalMarlas(), 0.01);
        assertEquals(5, result.getMarlasBreakdown()); // 5 * 225 = 1125, remaining = 75
        assertEquals(3, result.getSarsahiBreakdown()); // 3 * 25 = 75, remaining = 0
        assertEquals(0.0, result.getSqFtRemaining(), 0.01);
    }

    @Test
    public void testInputUnitGajYards() {
        // 10 Yards x 20 Yards = 200 Sq Yards = 1800 Sq Feet
        LandCalculationResult result = PakLandCalculator.calculateEqualSides(
                10.0, 10.0, 20.0, 20.0,
                InputUnit.YARDS_GAJ,
                MarlaStandard.STD_225
        );

        assertEquals(1800.0, result.getTotalSqFeet(), 0.01);
        assertEquals(200.0, result.getTotalSqYards(), 0.01);
    }

    @Test
    public void testInputUnitKaram() {
        // 2 Karam x 4 Karam -> (2 * 5.5) x (4 * 5.5) = 11 ft x 22 ft = 242 sq ft
        LandCalculationResult result = PakLandCalculator.calculateEqualSides(
                2.0, 2.0, 4.0, 4.0,
                InputUnit.KARAM,
                MarlaStandard.STD_225
        );

        assertEquals(242.0, result.getTotalSqFeet(), 0.01);
    }

    @Test
    public void testInvalidTriangleDiagonalThrows() {
        // Side A=10, Side B=10, Diagonal=50 -> Impossible triangle because 10 + 10 < 50
        assertThrows(IllegalArgumentException.class, () -> {
            PakLandCalculator.calculateHeronIrregular(
                    10.0, 10.0, 20.0, 20.0, 50.0,
                    InputUnit.FEET,
                    MarlaStandard.STD_225
            );
        });
    }

    @Test
    public void testQuickUnitConverter() {
        // 1 Kanal = 20 Marlas
        double marlas = PakLandCalculator.convertUnits(1.0, "KANAL", "MARLA", MarlaStandard.STD_225);
        assertEquals(20.0, marlas, 0.001);

        // 1 Acre = 8 Kanals
        double kanals = PakLandCalculator.convertUnits(1.0, "ACRE", "KANAL", MarlaStandard.STD_225);
        assertEquals(8.0, kanals, 0.001);

        // 1 Marla in 225 = 25 Sq Yards
        double gaj = PakLandCalculator.convertUnits(1.0, "MARLA", "SQ_YARDS", MarlaStandard.STD_225);
        assertEquals(25.0, gaj, 0.001);

        // 1 Marla in 272.25 = 30.25 Sq Yards
        double gajPatwari = PakLandCalculator.convertUnits(1.0, "MARLA", "SQ_YARDS", MarlaStandard.STD_272_25);
        assertEquals(30.25, gajPatwari, 0.001);
    }
}
