package com.land.measurement.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.land.measurement.R;
import com.land.measurement.model.CalculationMode;
import com.land.measurement.model.LandRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder> {

    public interface OnRecordActionListener {
        void onItemClick(LandRecord record);
        void onShareClick(LandRecord record);
        void onDeleteClick(LandRecord record);
    }

    private final Context context;
    private List<LandRecord> records = new ArrayList<>();
    private final OnRecordActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    public RecordsAdapter(Context context, OnRecordActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setRecords(List<LandRecord> newRecords) {
        this.records = newRecords != null ? newRecords : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        LandRecord record = records.get(position);

        String title = record.getTitle() != null && !record.getTitle().trim().isEmpty()
                ? record.getTitle().trim()
                : "Plot #" + record.getId() + " (" + String.format(Locale.US, "%.1f Marla", record.getTotalMarlas()) + ")";
        holder.tvTitle.setText(title);

        if (record.getLocation() != null && !record.getLocation().trim().isEmpty()) {
            holder.tvLocation.setText(record.getLocation().trim());
            holder.layoutLocation.setVisibility(View.VISIBLE);
        } else {
            holder.tvLocation.setText("Location not specified");
            holder.layoutLocation.setVisibility(View.VISIBLE);
        }

        holder.tvDate.setText(dateFormat.format(new Date(record.getTimestamp())));

        // Badges
        String modeStr = record.getCalculationMode() != null && record.getCalculationMode().contains("Heron")
                ? "Heron's Formula"
                : "Equal Sides";
        holder.tvModeBadge.setText(modeStr);

        String stdStr = record.getMarlaStandard() != null ? record.getMarlaStandard() : "272.25 Sq Ft";
        holder.tvStandardBadge.setText(stdStr);

        // Input unit helper
        String unitSymbol = "ft";
        if (record.getInputUnit() != null) {
            String u = record.getInputUnit().toLowerCase();
            if (u.contains("yd") || u.contains("gaj") || u.contains("yard")) {
                unitSymbol = "yd";
            } else if (u.contains("meter") || u.contains("m")) {
                unitSymbol = "m";
            } else if (u.contains("karam")) {
                unitSymbol = "karam";
            }
        }

        CalculationMode calcMode = (record.getCalculationMode() != null && record.getCalculationMode().toLowerCase().contains("heron"))
                ? CalculationMode.HERON_IRREGULAR
                : CalculationMode.EQUAL_SIDES;

        // Update Plot Shape Preview
        holder.plotPreviewRecord.updateDimensions(
                record.getSideA(),
                record.getSideB(),
                record.getSideC(),
                record.getSideD(),
                record.getDiagonal(),
                unitSymbol,
                calcMode
        );

        // Format Side Measurements
        StringBuilder sides = new StringBuilder();
        sides.append("Sides: A: ").append(record.getSideA()).append(" ").append(unitSymbol)
                .append("  |  B: ").append(record.getSideB()).append(" ").append(unitSymbol)
                .append("  |  C: ").append(record.getSideC()).append(" ").append(unitSymbol)
                .append("  |  D: ").append(record.getSideD()).append(" ").append(unitSymbol);
        if (calcMode == CalculationMode.HERON_IRREGULAR && record.getDiagonal() > 0) {
            sides.append("  |  Diag: ").append(record.getDiagonal()).append(" ").append(unitSymbol);
        }
        holder.tvMeasurements.setText(sides.toString());

        // Area & Breakdown
        holder.tvSqFt.setText(String.format(Locale.US, "%,.1f Sq Ft", record.getTotalSqFeet()));
        holder.tvMarlasKanals.setText(String.format(Locale.US, "%.2f Marlas (%.3f Kanals)", record.getTotalMarlas(), record.getTotalKanals()));
        holder.tvBreakdown.setText(record.getBreakdownSummary() != null ? record.getBreakdownSummary() : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(record);
        });

        holder.btnShare.setOnClickListener(v -> {
            if (listener != null) listener.onShareClick(record);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(record);
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        View layoutLocation;
        TextView tvTitle, tvLocation, tvDate, tvModeBadge, tvStandardBadge;
        PlotPreviewView plotPreviewRecord;
        TextView tvMeasurements, tvSqFt, tvMarlasKanals, tvBreakdown;
        MaterialButton btnShare, btnDelete;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutLocation = itemView.findViewById(R.id.layoutLocation);
            tvTitle = itemView.findViewById(R.id.tvRecordTitle);
            tvLocation = itemView.findViewById(R.id.tvRecordLocation);
            tvDate = itemView.findViewById(R.id.tvRecordDate);
            tvModeBadge = itemView.findViewById(R.id.tvModeBadge);
            tvStandardBadge = itemView.findViewById(R.id.tvStandardBadge);
            plotPreviewRecord = itemView.findViewById(R.id.plotPreviewRecord);
            tvMeasurements = itemView.findViewById(R.id.tvRecordMeasurements);
            tvSqFt = itemView.findViewById(R.id.tvRecordSqFt);
            tvMarlasKanals = itemView.findViewById(R.id.tvRecordMarlasKanals);
            tvBreakdown = itemView.findViewById(R.id.tvRecordBreakdown);
            btnShare = itemView.findViewById(R.id.btnShareRecord);
            btnDelete = itemView.findViewById(R.id.btnDeleteRecord);
        }
    }
}
