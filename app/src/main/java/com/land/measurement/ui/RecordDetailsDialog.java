package com.land.measurement.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.land.measurement.R;
import com.land.measurement.model.LandRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordDetailsDialog extends Dialog {

    private final LandRecord record;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault());

    public RecordDetailsDialog(@NonNull Context context, LandRecord record) {
        super(context);
        this.record = record;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_record_details);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvLocation = findViewById(R.id.tvDetailLocation);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvMode = findViewById(R.id.tvDetailMode);
        TextView tvStandard = findViewById(R.id.tvDetailStandard);
        TextView tvSides = findViewById(R.id.tvDetailSides);
        TextView tvSqFt = findViewById(R.id.tvDetailSqFt);
        TextView tvBreakdown = findViewById(R.id.tvDetailBreakdown);
        TextView tvConversions = findViewById(R.id.tvDetailConversions);
        MaterialButton btnClose = findViewById(R.id.btnDetailClose);
        MaterialButton btnShare = findViewById(R.id.btnDetailShare);

        tvTitle.setText(record.getTitle());
        tvLocation.setText(record.getLocation() != null && !record.getLocation().isEmpty() ? "Location: " + record.getLocation() : "Location: Not specified");
        tvDate.setText("Saved on: " + dateFormat.format(new Date(record.getTimestamp())));
        tvMode.setText(record.getCalculationMode());
        tvStandard.setText(record.getMarlaStandard());

        String unit = record.getInputUnit() != null ? record.getInputUnit() : "ft";
        StringBuilder sidesText = new StringBuilder();
        sidesText.append("• Side A: ").append(record.getSideA()).append(" ").append(unit).append("\n");
        sidesText.append("• Side B: ").append(record.getSideB()).append(" ").append(unit).append("\n");
        sidesText.append("• Side C: ").append(record.getSideC()).append(" ").append(unit).append("\n");
        sidesText.append("• Side D: ").append(record.getSideD()).append(" ").append(unit);
        if (record.getDiagonal() > 0) {
            sidesText.append("\n• Diagonal: ").append(record.getDiagonal()).append(" ").append(unit);
        }
        tvSides.setText(sidesText.toString());

        tvSqFt.setText(String.format(Locale.US, "%,.2f Square Feet", record.getTotalSqFeet()));
        tvBreakdown.setText(record.getBreakdownSummary());

        double sqYards = record.getTotalSqFeet() / 9.0;
        double sqMeters = record.getTotalSqFeet() / 10.7639104;
        double acres = record.getTotalKanals() / 8.0;
        tvConversions.setText(String.format(Locale.US,
                "• %,.2f Square Yards / Gaj\n• %,.2f Square Meters\n• %.4f Kanals\n• %.4f Acres",
                sqYards, sqMeters, record.getTotalKanals(), acres));

        btnClose.setOnClickListener(v -> dismiss());

        btnShare.setOnClickListener(v -> {
            shareRecord(getContext(), record);
        });
    }

    public static void shareRecord(Context context, LandRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("📐 *LAND MEASUREMENT REPORT*\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📍 *Title:* ").append(record.getTitle()).append("\n");
        if (record.getLocation() != null && !record.getLocation().isEmpty()) {
            sb.append("🏡 *Location/Owner:* ").append(record.getLocation()).append("\n");
        }
        sb.append("📏 *Method:* ").append(record.getCalculationMode()).append("\n");
        sb.append("🏷 *Standard:* ").append(record.getMarlaStandard()).append("\n");
        sb.append("────────────────────\n");
        sb.append("📐 *Dimensions:*\n");
        sb.append("  • Side A: ").append(record.getSideA()).append(" ").append(record.getInputUnit()).append("\n");
        sb.append("  • Side B: ").append(record.getSideB()).append(" ").append(record.getInputUnit()).append("\n");
        sb.append("  • Side C: ").append(record.getSideC()).append(" ").append(record.getInputUnit()).append("\n");
        sb.append("  • Side D: ").append(record.getSideD()).append(" ").append(record.getInputUnit()).append("\n");
        if (record.getDiagonal() > 0) {
            sb.append("  • Diagonal: ").append(record.getDiagonal()).append(" ").append(record.getInputUnit()).append("\n");
        }
        sb.append("────────────────────\n");
        sb.append("✨ *Total Area:*\n");
        sb.append("  ★ *").append(String.format(Locale.US, "%,.2f", record.getTotalSqFeet())).append(" Square Feet*\n");
        sb.append("  ★ *").append(record.getBreakdownSummary()).append("*\n");
        sb.append("  • Total Marlas: ").append(String.format(Locale.US, "%.3f", record.getTotalMarlas())).append(" Marlas\n");
        sb.append("  • Total Kanals: ").append(String.format(Locale.US, "%.4f", record.getTotalKanals())).append(" Kanals\n");
        sb.append("  • Sq Yards / Gaj: ").append(String.format(Locale.US, "%,.2f", record.getTotalSqFeet() / 9.0)).append(" Gaj\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("📱 _Pak Land Measurement App_");

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Land Measurement: " + record.getTitle());
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        context.startActivity(Intent.createChooser(sendIntent, "Share Land Measurement"));
    }
}
