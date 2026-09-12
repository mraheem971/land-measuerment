package com.land.measurement.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.land.measurement.R;
import com.land.measurement.db.LandDatabaseHelper;
import com.land.measurement.model.LandCalculationResult;
import com.land.measurement.model.LandRecord;

import java.util.Locale;

public class SaveRecordDialog extends Dialog {

    public interface OnRecordSavedListener {
        void onRecordSaved();
    }

    private final LandCalculationResult result;
    private final OnRecordSavedListener listener;

    public SaveRecordDialog(@NonNull Context context, LandCalculationResult result, OnRecordSavedListener listener) {
        super(context);
        this.result = result;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_save_record);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText etTitle = findViewById(R.id.etDialogTitle);
        EditText etLocation = findViewById(R.id.etDialogLocation);
        TextView tvAreaPreview = findViewById(R.id.tvDialogAreaPreview);
        TextView tvBreakdownPreview = findViewById(R.id.tvDialogBreakdownPreview);
        MaterialButton btnCancel = findViewById(R.id.btnCancelSave);
        MaterialButton btnSave = findViewById(R.id.btnConfirmSave);

        tvAreaPreview.setText(String.format(Locale.US, "Area: %,.1f Sq Ft (%.2f Marlas)", result.getTotalSqFeet(), result.getTotalMarlas()));
        tvBreakdownPreview.setText("Breakdown: " + result.getFormattedBreakdown());

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                title = "Plot (" + String.format(Locale.US, "%.1f Marla", result.getTotalMarlas()) + ")";
            }
            String location = etLocation.getText().toString().trim();

            LandRecord record = new LandRecord(
                    0,
                    title,
                    location,
                    result.getTotalSqFeet(),
                    result.getTotalMarlas(),
                    result.getTotalKanals(),
                    result.getFormattedBreakdown(),
                    result.getMode().getEnglishName(),
                    result.getMarlaStandard().getDisplayName(),
                    result.getInputUnit().getDisplayName(),
                    result.getSideA(),
                    result.getSideB(),
                    result.getSideC(),
                    result.getSideD(),
                    result.getDiagonal(),
                    System.currentTimeMillis()
            );

            long rowId = LandDatabaseHelper.getInstance(getContext()).insertRecord(record);
            if (rowId > 0) {
                Toast.makeText(getContext(), R.string.record_saved_success, Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onRecordSaved();
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to save record.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
