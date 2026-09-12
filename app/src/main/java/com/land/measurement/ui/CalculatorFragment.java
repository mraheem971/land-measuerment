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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
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
    private ChipGroup chipGroupStandard;
    private ChipGroup chipGroupUnit;
    private RadioGroup rgCalculationMode;
    private RadioButton rbModeEqual;
    private RadioButton rbModeHeron;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        initViews(view);
        setupListeners();
        updateHintsAndPreview();
        return view;
    }

    private void initViews(View v) {
        scrollViewCalculator = v.findViewById(R.id.scrollViewCalculator);
        chipGroupStandard = v.findViewById(R.id.chipGroupStandard);
        chipGroupUnit = v.findViewById(R.id.chipGroupUnit);
        rgCalculationMode = v.findViewById(R.id.rgCalculationMode);
        rbModeEqual = v.findViewById(R.id.rbModeEqual);
        rbModeHeron = v.findViewById(R.id.rbModeHeron);

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

    private void setupListeners() {
        chipGroupStandard.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipStd225)) {
                currentStandard = MarlaStandard.STD_225;
            } else if (checkedIds.contains(R.id.chipStd272)) {
                currentStandard = MarlaStandard.STD_272_25;
            } else if (checkedIds.contains(R.id.chipStd250)) {
                currentStandard = MarlaStandard.STD_250;
            }
            if (lastResult != null) {
                performCalculation();
            }
        });

        chipGroupUnit.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipUnitFeet)) {
                currentUnit = InputUnit.FEET;
            } else if (checkedIds.contains(R.id.chipUnitYards)) {
                currentUnit = InputUnit.YARDS_GAJ;
            } else if (checkedIds.contains(R.id.chipUnitMeters)) {
                currentUnit = InputUnit.METERS;
            } else if (checkedIds.contains(R.id.chipUnitKaram)) {
                currentUnit = InputUnit.KARAM;
            }
            updateHintsAndPreview();
            if (lastResult != null) {
                performCalculation();
            }
        });

        rgCalculationMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbModeEqual) {
                currentMode = CalculationMode.EQUAL_SIDES;
                tilDiagonal.setVisibility(View.GONE);
            } else {
                currentMode = CalculationMode.HERON_IRREGULAR;
                tilDiagonal.setVisibility(View.VISIBLE);
            }
            updateHintsAndPreview();
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

        // Auto scroll to focused input view so soft keyboard doesn't obscure it
        setupFocusScroll(etSideA, tilSideA);
        setupFocusScroll(etSideB, tilSideB);
        setupFocusScroll(etSideC, tilSideC);
        setupFocusScroll(etSideD, tilSideD);
        setupFocusScroll(etDiagonal, tilDiagonal);

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

    private void setupFocusScroll(TextInputEditText editText, TextInputLayout til) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && scrollViewCalculator != null) {
                scrollViewCalculator.postDelayed(() -> {
                    if (isAdded() && scrollViewCalculator != null) {
                        scrollViewCalculator.smoothScrollTo(0, til.getTop() - 40);
                    }
                }, 250);
            }
        });
    }

    private void updateHintsAndPreview() {
        String unitShort = " (" + currentUnit.getShortSymbol() + ")";
        tvLiveUnitBadge.setText("Unit: " + currentUnit.getDisplayName());

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
