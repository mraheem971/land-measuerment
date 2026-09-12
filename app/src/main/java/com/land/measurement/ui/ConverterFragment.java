package com.land.measurement.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.ChipGroup;
import com.land.measurement.R;
import com.land.measurement.calculator.PakLandCalculator;
import com.land.measurement.model.MarlaStandard;

import java.util.Locale;

public class ConverterFragment extends Fragment {

    private NestedScrollView scrollViewConverter;
    private ChipGroup chipGroupConverterStandard;
    private EditText etConverterValue;
    private Spinner spinnerFromUnit;

    private TextView tvConvSqFt, tvConvMarlas, tvConvKanals, tvConvGaj, tvConvSqMeters, tvConvSarsahi, tvConvAcres;

    private MarlaStandard currentStandard = MarlaStandard.STD_272_25;

    private final String[] unitKeys = {
            "MARLA", "KANAL", "SQ_FEET", "SQ_YARDS", "SQ_METERS", "SARSAHI", "ACRE", "MURABBA"
    };

    private final String[] unitDisplay = {
            "Marla",
            "Kanal",
            "Square Feet (sq ft)",
            "Gaj / Square Yards (sq yd)",
            "Square Meters (sq m)",
            "Sarsahi",
            "Acre / Qila",
            "Murabba (25 Acres)"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);
        initViews(view);
        setupListeners();
        recalculate();
        return view;
    }

    private void initViews(View v) {
        scrollViewConverter = v.findViewById(R.id.scrollViewConverter);
        chipGroupConverterStandard = v.findViewById(R.id.chipGroupConverterStandard);
        etConverterValue = v.findViewById(R.id.etConverterValue);
        spinnerFromUnit = v.findViewById(R.id.spinnerFromUnit);

        tvConvSqFt = v.findViewById(R.id.tvConvSqFt);
        tvConvMarlas = v.findViewById(R.id.tvConvMarlas);
        tvConvKanals = v.findViewById(R.id.tvConvKanals);
        tvConvGaj = v.findViewById(R.id.tvConvGaj);
        tvConvSqMeters = v.findViewById(R.id.tvConvSqMeters);
        tvConvSarsahi = v.findViewById(R.id.tvConvSarsahi);
        tvConvAcres = v.findViewById(R.id.tvConvAcres);

        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, unitDisplay);
            spinnerFromUnit.setAdapter(adapter);
        }
    }

    private void setupListeners() {
        chipGroupConverterStandard.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipConvStd225)) {
                currentStandard = MarlaStandard.STD_225;
            } else if (checkedIds.contains(R.id.chipConvStd272)) {
                currentStandard = MarlaStandard.STD_272_25;
            } else if (checkedIds.contains(R.id.chipConvStd250)) {
                currentStandard = MarlaStandard.STD_250;
            }
            recalculate();
        });

        etConverterValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                recalculate();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etConverterValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && scrollViewConverter != null) {
                scrollViewConverter.postDelayed(() -> {
                    if (isAdded() && scrollViewConverter != null) {
                        scrollViewConverter.smoothScrollTo(0, etConverterValue.getTop() - 40);
                    }
                }, 250);
            }
        });

        spinnerFromUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recalculate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void recalculate() {
        String valStr = etConverterValue.getText().toString().trim();
        if (valStr.isEmpty()) {
            tvConvSqFt.setText("0");
            tvConvMarlas.setText("0");
            tvConvKanals.setText("0");
            tvConvGaj.setText("0");
            tvConvSqMeters.setText("0");
            tvConvSarsahi.setText("0");
            tvConvAcres.setText("0");
            return;
        }

        double val;
        try {
            val = Double.parseDouble(valStr);
        } catch (NumberFormatException e) {
            return;
        }

        int pos = spinnerFromUnit.getSelectedItemPosition();
        if (pos < 0 || pos >= unitKeys.length) pos = 0;
        String fromUnit = unitKeys[pos];

        double sqFt = PakLandCalculator.convertUnits(val, fromUnit, "SQ_FEET", currentStandard);
        double marlas = PakLandCalculator.convertUnits(val, fromUnit, "MARLA", currentStandard);
        double kanals = PakLandCalculator.convertUnits(val, fromUnit, "KANAL", currentStandard);
        double gaj = PakLandCalculator.convertUnits(val, fromUnit, "SQ_YARDS", currentStandard);
        double sqMeters = PakLandCalculator.convertUnits(val, fromUnit, "SQ_METERS", currentStandard);
        double sarsahi = PakLandCalculator.convertUnits(val, fromUnit, "SARSAHI", currentStandard);
        double acres = PakLandCalculator.convertUnits(val, fromUnit, "ACRE", currentStandard);

        tvConvSqFt.setText(String.format(Locale.US, "%,.2f", sqFt));
        tvConvMarlas.setText(String.format(Locale.US, "%.4f", marlas));
        tvConvKanals.setText(String.format(Locale.US, "%.4f", kanals));
        tvConvGaj.setText(String.format(Locale.US, "%,.2f", gaj));
        tvConvSqMeters.setText(String.format(Locale.US, "%,.2f", sqMeters));
        tvConvSarsahi.setText(String.format(Locale.US, "%.2f", sarsahi));
        tvConvAcres.setText(String.format(Locale.US, "%.6f", acres));
    }
}
