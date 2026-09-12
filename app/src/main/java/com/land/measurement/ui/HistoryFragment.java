package com.land.measurement.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.land.measurement.R;
import com.land.measurement.db.LandDatabaseHelper;
import com.land.measurement.model.LandRecord;

import java.util.List;

public class HistoryFragment extends Fragment implements RecordsAdapter.OnRecordActionListener {

    private EditText etSearchRecords;
    private TextView tvRecordsCount;
    private MaterialButton btnClearHistory;
    private LinearLayout layoutEmptyState;
    private RecyclerView rvRecords;
    private RecordsAdapter adapter;
    private LandDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecords();
    }

    private void initViews(View v) {
        dbHelper = LandDatabaseHelper.getInstance(requireContext());

        etSearchRecords = v.findViewById(R.id.etSearchRecords);
        tvRecordsCount = v.findViewById(R.id.tvRecordsCount);
        btnClearHistory = v.findViewById(R.id.btnClearHistory);
        layoutEmptyState = v.findViewById(R.id.layoutEmptyState);
        rvRecords = v.findViewById(R.id.rvRecords);

        rvRecords.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecordsAdapter(requireContext(), this);
        rvRecords.setAdapter(adapter);
    }

    private void setupListeners() {
        etSearchRecords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                loadRecords();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearHistory.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Clear All Records")
                    .setMessage("Are you sure you want to delete all saved land measurements? This cannot be undone.")
                    .setPositiveButton("Clear All", (dialog, which) -> {
                        dbHelper.clearAllRecords();
                        loadRecords();
                        Toast.makeText(getContext(), "All records deleted.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    public void loadRecords() {
        if (dbHelper == null) return;
        String query = etSearchRecords != null ? etSearchRecords.getText().toString().trim() : "";
        List<LandRecord> records = dbHelper.searchRecords(query);

        if (records.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvRecords.setVisibility(View.GONE);
            tvRecordsCount.setText("Saved Plots: 0");
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvRecords.setVisibility(View.VISIBLE);
            tvRecordsCount.setText("Saved Plots: " + records.size());
        }

        adapter.setRecords(records);
    }

    @Override
    public void onItemClick(LandRecord record) {
        if (getContext() != null) {
            RecordDetailsDialog dialog = new RecordDetailsDialog(getContext(), record);
            dialog.show();
        }
    }

    @Override
    public void onShareClick(LandRecord record) {
        if (getContext() != null) {
            RecordDetailsDialog.shareRecord(getContext(), record);
        }
    }

    @Override
    public void onDeleteClick(LandRecord record) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Record")
                .setMessage("Delete '" + record.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteRecord(record.getId());
                    loadRecords();
                    Toast.makeText(getContext(), "Record deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
