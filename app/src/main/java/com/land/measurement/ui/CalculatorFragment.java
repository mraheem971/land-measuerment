package com.land.measurement.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.land.measurement.R;
import com.land.measurement.calculator.PakLandCalculator;
import com.land.measurement.model.CalculationMode;
import com.land.measurement.model.InputUnit;
import com.land.measurement.model.LandCalculationResult;
import com.land.measurement.model.MarlaStandard;

import java.util.Locale;

public class CalculatorFragment extends Fragment {

    private NestedScrollView scrollViewCalculator;
    private Spinner spinnerCalculationMode;
    private Spinner spinnerMarlaStandard;
    private Spinner spinnerInputUnit;

    private TextInputLayout tilSideA, tilSideB, tilSideC, tilSideD, tilDiagonal;
    private TextInputEditText etSideA, etSideB, etSideC, etSideD, etDiagonal;
    private TextView tvLiveUnitBadge;
    private PlotPreviewView plotPreview;

    private MaterialButton btnCalculate, btnClear;
    private MaterialCardView cardResults;
    private TextView tvResultStandardTag, tvResultSqFeet, tvResultBreakdownEn;
    private TextView tvResultMarlas, tvResultKanals, tvResultGaj;
    private MaterialButton btnSaveRecord, btnShareResult, btnCopyResult;

    private MarlaStandard currentStandard = MarlaStandard.STD_272_25;
    private InputUnit currentUnit = InputUnit.FEET;
    private CalculationMode currentMode = CalculationMode.HERON_IRREGULAR;
    private LandCalculationResult lastResult;

    private final String[] modeOptions = {
            "4 Irregular Sides + Diagonal (Heron's Formula)",
            "Equal / Regular 4 Sides (Average Method)"
    };

    private final String[] standardOptions = {
            "272.25 Sq Ft (Patwari Standard)",
            "225 Sq Ft (Punjab / Housing Schemes)",
            "250 Sq Ft (Custom)"
    };

    private final String[] unitOptions = {
            "Feet (ft)",
            "Gaj / Yards (yd)",
            "Meters (m)",
            "Karam (5.5 ft)"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        initViews(view);
        setupSpinners();
        setupListeners();
        updateHintsAndPreview();
        return view;
    }

    private void initViews(View v) {
        scrollViewCalculator = v.findViewById(R.id.scrollViewCalculator);
        spinnerCalculationMode = v.findViewById(R.id.spinnerCalculationMode);
        spinnerMarlaStandard = v.findViewById(R.id.spinnerMarlaStandard);
        spinnerInputUnit = v.findViewById(R.id.spinnerInputUnit);

        tilSideA = v.findViewById(R.id.tilSideA);
        tilSideB = v.findViewById(R.id.tilSideB);
        tilSideC = v.findViewById(R.id.tilSideC);
        tilSideD = v.findViewById(R.id.tilSideD);
        tilDiagonal = v.findViewById(R.id.tilDiagonal);

        etSideA = v.findViewById(R.id.etSideA);
        etSideB = v.findViewById(R.id.etSideB);
        etSideC = v.findViewById(R.id.etSideC);
        etSideD = v.findViewById(R.id.etSideD);
        etDiagonal = v.findViewById(R.id.etDiagonal);

        tvLiveUnitBadge = v.findViewById(R.id.tvLiveUnitBadge);
        plotPreview = v.findViewById(R.id.plotPreview);

        btnCalculate = v.findViewById(R.id.btnCalculate);
        btnClear = v.findViewById(R.id.btnClear);

        cardResults = v.findViewById(R.id.cardResults);
        tvResultStandardTag = v.findViewById(R.id.tvResultStandardTag);
        tvResultSqFeet = v.findViewById(R.id.tvResultSqFeet);
        tvResultBreakdownEn = v.findViewById(R.id.tvResultBreakdownEn);
        tvResultMarlas = v.findViewById(R.id.tvResultMarlas);
        tvResultKanals = v.findViewById(R.id.tvResultKanals);
        tvResultGaj = v.findViewById(R.id.tvResultGaj);

        btnSaveRecord = v.findViewById(R.id.btnSaveRecord);
        btnShareResult = v.findViewById(R.id.btnShareResult);
        btnCopyResult = v.findViewById(R.id.btnCopyResult);
    }

    private void setupSpinners() {
        if (getContext() == null) return;

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_selected, modeOptions);
        modeAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerCalculationMode.setAdapter(modeAdapter);
        spinnerCalculationMode.setSelection(0);

        ArrayAdapter<String> standardAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_selected, standardOptions);
        standardAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerMarlaStandard.setAdapter(standardAdapter);
        spinnerMarlaStandard.setSelection(0); // 272.25 default

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_selected, unitOptions);
        unitAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinnerInputUnit.setAdapter(unitAdapter);
        spinnerInputUnit.setSelection(0); // Feet default
    }

    private void setupListeners() {
        spinnerCalculationMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    currentMode = CalculationMode.HERON_IRREGULAR;
                    tilDiagonal.setVisibility(View.VISIBLE);
                } else {
                    currentMode = CalculationMode.EQUAL_SIDES;
                    tilDiagonal.setVisibility(View.GONE);
                }
                updateHintsAndPreview();
                if (lastResult != null) {
                    performCalculation();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMarlaStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        currentStandard = MarlaStandard.STD_225;
                        break;
                    case 2:
                        currentStandard = MarlaStandard.STD_250;
                        break;
                    case 0:
                    default:
                        currentStandard = MarlaStandard.STD_272_25;
                        break;
                }
                if (lastResult != null) {
                    performCalculation();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerInputUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        currentUnit = InputUnit.YARDS_GAJ;
                        break;
                    case 2:
                        currentUnit = InputUnit.METERS;
                        break;
                    case 3:
                        currentUnit = InputUnit.KARAM;
                        break;
                    case 0:
                    default:
                        currentUnit = InputUnit.FEET;
                        break;
                }
                updateHintsAndPreview();
                if (lastResult != null) {
                    performCalculation();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                updatePlotPreviewLive();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etSideA.addTextChangedListener(textWatcher);
        etSideB.addTextChangedListener(textWatcher);
        etSideC.addTextChangedListener(textWatcher);
        etSideD.addTextChangedListener(textWatcher);
        etDiagonal.addTextChangedListener(textWatcher);

        btnCalculate.setOnClickListener(v -> performCalculation());
        btnClear.setOnClickListener(v -> clearFields());

        btnSaveRecord.setOnClickListener(v -> {
            if (lastResult != null && getContext() != null) {
                SaveRecordDialog dialog = new SaveRecordDialog(getContext(), lastResult, () -> {
                    // Saved successfully
                });
                dialog.show();
            }
        });

        btnShareResult.setOnClickListener(v -> shareCalculatedResult());
        btnCopyResult.setOnClickListener(v -> copyCalculatedResult());
    }

    private void updateHintsAndPreview() {
        String unitShort = " (" + currentUnit.getShortSymbol() + ")";
        tvLiveUnitBadge.setText("Unit: " + currentUnit.getShortSymbol());

        if (currentMode == CalculationMode.EQUAL_SIDES) {
            tilSideA.setHint("Length 1" + unitShort);
            tilSideB.setHint("Width 1" + unitShort);
            tilSideC.setHint("Length 2" + unitShort);
            tilSideD.setHint("Width 2" + unitShort);
        } else {
            tilSideA.setHint("Side A (North / Front)" + unitShort);
            tilSideB.setHint("Side B (East / Right)" + unitShort);
            tilSideC.setHint("Side C (South / Back)" + unitShort);
            tilSideD.setHint("Side D (West / Left)" + unitShort);
            tilDiagonal.setHint("Diagonal (Corner A to C)" + unitShort);
        }

        updatePlotPreviewLive();
    }

    private void updatePlotPreviewLive() {
        double a = parseDouble(etSideA.getText().toString(), 40);
        double b = parseDouble(etSideB.getText().toString(), 60);
        double c = parseDouble(etSideC.getText().toString(), 40);
        double d = parseDouble(etSideD.getText().toString(), 60);
        double diag = parseDouble(etDiagonal.getText().toString(), 0);

        plotPreview.updateDimensions(a, b, c, d, diag, currentUnit.getShortSymbol(), currentMode);
    }

    private void performCalculation() {
        String sA = etSideA.getText().toString().trim();
        String sB = etSideB.getText().toString().trim();
        String sC = etSideC.getText().toString().trim();
        String sD = etSideD.getText().toString().trim();
        String sDiag = etDiagonal.getText().toString().trim();

        if (sA.isEmpty() || sB.isEmpty()) {
            Toast.makeText(getContext(), R.string.err_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        double valA, valB, valC, valD, valDiag = 0;
        try {
            valA = Double.parseDouble(sA);
            valB = Double.parseDouble(sB);
            valC = sC.isEmpty() ? valA : Double.parseDouble(sC);
            valD = sD.isEmpty() ? valB : Double.parseDouble(sD);
            if (currentMode == CalculationMode.HERON_IRREGULAR) {
                if (sDiag.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter the diagonal measurement.", Toast.LENGTH_SHORT).show();
                    return;
                }
                valDiag = Double.parseDouble(sDiag);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), R.string.err_invalid_number, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (currentMode == CalculationMode.EQUAL_SIDES) {
                lastResult = PakLandCalculator.calculateEqualSides(valA, valC, valB, valD, currentUnit, currentStandard);
            } else {
                lastResult = PakLandCalculator.calculateHeronIrregular(valA, valB, valC, valD, valDiag, currentUnit, currentStandard);
            }

            displayResult(lastResult);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayResult(LandCalculationResult res) {
        cardResults.setVisibility(View.VISIBLE);
        tvResultStandardTag.setText(res.getMarlaStandard().getDisplayName());
        tvResultSqFeet.setText(String.format(Locale.US, "%,.2f Sq Ft", res.getTotalSqFeet()));
        tvResultBreakdownEn.setText(res.getFormattedBreakdown());

        tvResultMarlas.setText(String.format(Locale.US, "%.3f", res.getTotalMarlas()));
        tvResultKanals.setText(String.format(Locale.US, "%.4f", res.getTotalKanals()));
        tvResultGaj.setText(String.format(Locale.US, "%,.2f", res.getTotalSqYards()));

        if (scrollViewCalculator != null) {
            scrollViewCalculator.postDelayed(() -> {
                if (isAdded() && scrollViewCalculator != null) {
                    scrollViewCalculator.smoothScrollTo(0, cardResults.getTop() - 20);
                }
            }, 100);
        }
    }

    private void clearFields() {
        etSideA.setText("");
        etSideB.setText("");
        etSideC.setText("");
        etSideD.setText("");
        etDiagonal.setText("");
        cardResults.setVisibility(View.GONE);
        lastResult = null;
        updatePlotPreviewLive();
    }

    private void shareCalculatedResult() {
        if (lastResult == null || getContext() == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("📐 *PAK LAND MEASUREMENT REPORT*\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📏 *Method:* ").append(lastResult.getMode().getEnglishName()).append("\n");
        sb.append("🏷 *Standard:* ").append(lastResult.getMarlaStandard().getDisplayName()).append("\n");
        sb.append("────────────────────\n");
        sb.append("📐 *Dimensions:*\n");
        sb.append("  • Side A: ").append(lastResult.getSideA()).append(" ").append(lastResult.getInputUnit().getShortSymbol()).append("\n");
        sb.append("  • Side B: ").append(lastResult.getSideB()).append(" ").append(lastResult.getInputUnit().getShortSymbol()).append("\n");
        sb.append("  • Side C: ").append(lastResult.getSideC()).append(" ").append(lastResult.getInputUnit().getShortSymbol()).append("\n");
        sb.append("  • Side D: ").append(lastResult.getSideD()).append(" ").append(lastResult.getInputUnit().getShortSymbol()).append("\n");
        if (lastResult.getDiagonal() > 0) {
            sb.append("  • Diagonal: ").append(lastResult.getDiagonal()).append(" ").append(lastResult.getInputUnit().getShortSymbol()).append("\n");
        }
        sb.append("────────────────────\n");
        sb.append("✨ *Calculated Area:*\n");
        sb.append("  ★ *").append(String.format(Locale.US, "%,.2f", lastResult.getTotalSqFeet())).append(" Square Feet*\n");
        sb.append("  ★ *").append(lastResult.getFormattedBreakdown()).append("*\n");
        sb.append("  • Total Marlas: ").append(String.format(Locale.US, "%.3f", lastResult.getTotalMarlas())).append(" Marlas\n");
        sb.append("  • Total Kanals: ").append(String.format(Locale.US, "%.4f", lastResult.getTotalKanals())).append(" Kanals\n");
        sb.append("  • Gaj / Sq Yards: ").append(String.format(Locale.US, "%,.2f", lastResult.getTotalSqYards())).append(" Gaj\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📱 _Calculated via Pak Land Measurement App_");

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(sendIntent, "Share Land Measurement"));
    }

    private void copyCalculatedResult() {
        if (lastResult == null || getContext() == null) return;
        String text = String.format(Locale.US,
                "Area: %,.2f Sq Ft | %s | %.3f Marlas | %.4f Kanals",
                lastResult.getTotalSqFeet(),
                lastResult.getFormattedBreakdown(),
                lastResult.getTotalMarlas(),
                lastResult.getTotalKanals()
        );
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Land Measurement", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Measurement copied to clipboard!", Toast.LENGTH_SHORT).show();
        }
    }

    private double parseDouble(String str, double defVal) {
        if (str == null || str.trim().isEmpty()) return defVal;
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }
}
