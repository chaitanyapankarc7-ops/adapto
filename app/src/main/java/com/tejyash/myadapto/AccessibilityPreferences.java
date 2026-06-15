package com.tejyash.myadapto;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Single source of truth for all accessibility sizing preferences.
 * Used by both MainActivity4 (settings) and HomeActivity (launcher grid).
 */
public class AccessibilityPreferences {

    private static final String PREFS_NAME    = "adapto_accessibility";
    private static final String KEY_FONT_STEP = "font_step";
    private static final String KEY_ICON_STEP = "icon_step";
    private static final String KEY_GRID_COLS = "grid_columns";

    // Text size steps (sp) — matches the 4 steps in the existing seekBar2
    public static final float[] TEXT_SIZES   = {14f, 18f, 22f, 28f};
    // Icon size steps (dp) — matches the 4 steps in the existing seekBar3
    public static final int[]   ICON_SIZES   = {40, 52, 64, 80};
    // Grid column options
    public static final int     GRID_MIN     = 2;
    public static final int     GRID_MAX     = 5;

    public static final int DEFAULT_FONT_STEP = 1;   // 18sp
    public static final int DEFAULT_ICON_STEP = 1;   // 52dp
    public static final int DEFAULT_GRID_COLS = 4;

    private static AccessibilityPreferences instance;

    public static AccessibilityPreferences get(Context ctx) {
        if (instance == null) {
            instance = new AccessibilityPreferences(ctx.getApplicationContext());
        }
        return instance;
    }

    private final SharedPreferences prefs;

    private AccessibilityPreferences(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int  getFontStep() { return prefs.getInt(KEY_FONT_STEP, DEFAULT_FONT_STEP); }
    public int  getIconStep() { return prefs.getInt(KEY_ICON_STEP, DEFAULT_ICON_STEP); }
    public int  getGridCols() { return prefs.getInt(KEY_GRID_COLS, DEFAULT_GRID_COLS); }

    public float getFontSizeSp() { return TEXT_SIZES[getFontStep()]; }
    public int   getIconSizeDp() { return ICON_SIZES[getIconStep()]; }

    public void setFontStep(int step) {
        prefs.edit().putInt(KEY_FONT_STEP, clamp(step, 0, TEXT_SIZES.length - 1)).apply();
        notifyListeners();
    }

    public void setIconStep(int step) {
        prefs.edit().putInt(KEY_ICON_STEP, clamp(step, 0, ICON_SIZES.length - 1)).apply();
        notifyListeners();
    }

    public void setGridCols(int cols) {
        prefs.edit().putInt(KEY_GRID_COLS, clamp(cols, GRID_MIN, GRID_MAX)).apply();
        notifyListeners();
    }

    // ── Listener support so HomeActivity updates in real-time ──────
    public interface OnPrefsChangedListener {
        void onPrefsChanged();
    }

    private OnPrefsChangedListener listener;

    public void setListener(OnPrefsChangedListener l) { this.listener = l; }
    public void clearListener()                        { this.listener = null; }

    private void notifyListeners() {
        if (listener != null) listener.onPrefsChanged();
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
