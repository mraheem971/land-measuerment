package com.land.measurement.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.land.measurement.R;

public class LandGuideDialog extends Dialog {

    public LandGuideDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_land_guide);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialButton btnClose = findViewById(R.id.btnGuideClose);
        btnClose.setOnClickListener(v -> dismiss());
    }
}
