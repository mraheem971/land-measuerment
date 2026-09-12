package com.land.measurement;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.land.measurement.ui.CalculatorFragment;
import com.land.measurement.ui.ConverterFragment;
import com.land.measurement.ui.HistoryFragment;
import com.land.measurement.ui.LandGuideDialog;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnHeaderInfo;

    private final String[] tabTitles = {
            "Calculator",
            "Converter",
            "Saved Records"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle both system navigation bars and soft keyboard (IME) insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            int bottomInset = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomInset);
            return insets;
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnHeaderInfo = findViewById(R.id.btnHeaderInfo);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(tabTitles[position]);
        }).attach();

        btnHeaderInfo.setOnClickListener(v -> {
            LandGuideDialog dialog = new LandGuideDialog(this);
            dialog.show();
        });
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {

        public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1:
                    return new ConverterFragment();
                case 2:
                    return new HistoryFragment();
                case 0:
                default:
                    return new CalculatorFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}