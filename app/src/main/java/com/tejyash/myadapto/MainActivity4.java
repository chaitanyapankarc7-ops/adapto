package com.tejyash.myadapto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity4 extends AppCompatActivity {

    // steps match AccessibilityPreferences arrays
    private static final float[] TEXT_SIZES   = AccessibilityPreferences.TEXT_SIZES;
    private static final int[]   ICON_SIZES_DP = AccessibilityPreferences.ICON_SIZES;

    private AccessibilityPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);

        prefs = AccessibilityPreferences.get(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.button5), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SeekBar   seekTextSize   = findViewById(R.id.seekBar2);
        SeekBar   seekIconSize   = findViewById(R.id.seekBar3);
        Switch    switchContrast = findViewById(R.id.switch1);
        TextView  tvLargeA      = findViewById(R.id.textView20);
        ImageView imgIconBig    = findViewById(R.id.imageView8);
        CardView  card4         = findViewById(R.id.card4);
        TextView  tvContrastLabel = findViewById(R.id.textView16);
        TextView  tvContrastSub   = findViewById(R.id.textView22);

        // ── Restore saved values ────────────────────────────────────
        seekTextSize.setMax(3);
        seekTextSize.setProgress(prefs.getFontStep());

        seekIconSize.setMax(3);
        seekIconSize.setProgress(prefs.getIconStep());

        // Apply saved values to preview immediately
        tvLargeA.setTextSize(TEXT_SIZES[prefs.getFontStep()] + 10);
        applyIconPreview(imgIconBig, ICON_SIZES_DP[prefs.getIconStep()]);

        // ── Text Size — save + update preview ──────────────────────
        seekTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float size = TEXT_SIZES[progress];
                tvLargeA.setTextSize(size + 10);
                if (fromUser) prefs.setFontStep(progress); // persists + notifies HomeActivity
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        // ── Icon Size — save + update preview ──────────────────────
        seekIconSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int sizeDp = ICON_SIZES_DP[progress];
                applyIconPreview(imgIconBig, sizeDp);
                if (fromUser) prefs.setIconStep(progress); // persists + notifies HomeActivity
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        // ── Contrast toggle (unchanged behaviour) ──────────────────
        switchContrast.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                card4.setCardBackgroundColor(0xFF000000);
                tvContrastLabel.setTextColor(0xFFFFFFFF);
                tvContrastSub.setTextColor(0xFFFFFFFF);
                tvContrastLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                card4.setCardBackgroundColor(0xFFFFFFFF);
                tvContrastLabel.setTextColor(0xFF0F120B);
                tvContrastSub.setTextColor(0xFF000000);
                tvContrastLabel.setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        });
    }

    private void applyIconPreview(ImageView img, int dp) {
        int px = dpToPx(dp);
        android.view.ViewGroup.LayoutParams params = img.getLayoutParams();
        params.width  = px;
        params.height = px;
        img.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
