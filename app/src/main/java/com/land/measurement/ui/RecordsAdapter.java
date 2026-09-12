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

        holder.tvTitle.setText(record.getTitle() != null && !record.getTitle().isEmpty() ? record.getTitle() : "Untitled Plot");
        
        if (record.getLocation() != null && !record.getLocation().isEmpty()) {
            holder.tvLocation.setText(record.getLocation());
            holder.tvLocation.setVisibility(View.VISIBLE);
        } else {
            holder.tvLocation.setVisibility(View.GONE);
        }

        holder.tvDate.setText(dateFormat.format(new Date(record.getTimestamp())));
        holder.tvModeBadge.setText(record.getCalculationMode());
        holder.tvStandardBadge.setText(record.getMarlaStandard());

        holder.tvSqFt.setText(String.format(Locale.US, "%,.1f Sq Ft", record.getTotalSqFeet()));
        holder.tvBreakdown.setText(record.getBreakdownSummary());

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
        TextView tvTitle, tvLocation, tvDate, tvModeBadge, tvStandardBadge, tvSqFt, tvBreakdown;
        MaterialButton btnShare, btnDelete;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvRecordTitle);
            tvLocation = itemView.findViewById(R.id.tvRecordLocation);
            tvDate = itemView.findViewById(R.id.tvRecordDate);
            tvModeBadge = itemView.findViewById(R.id.tvModeBadge);
            tvStandardBadge = itemView.findViewById(R.id.tvStandardBadge);
            tvSqFt = itemView.findViewById(R.id.tvRecordSqFt);
            tvBreakdown = itemView.findViewById(R.id.tvRecordBreakdown);
            btnShare = itemView.findViewById(R.id.btnShareRecord);
            btnDelete = itemView.findViewById(R.id.btnDeleteRecord);
        }
    }
}
