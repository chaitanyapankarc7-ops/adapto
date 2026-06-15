package com.tejyash.myadapto;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The HOME launcher screen.
 *
 * Registered in AndroidManifest.xml with:
 *   android.intent.category.HOME + android.intent.category.DEFAULT
 *
 * Listens to AccessibilityPreferences so font size, icon size, and column
 * count all update in real-time when the user changes them in MainActivity4.
 */
public class HomeActivity extends AppCompatActivity
        implements AccessibilityPreferences.OnPrefsChangedListener {

    private AppGridAdapter    adapter;
    private GridLayoutManager layoutManager;
    private List<AppInfo>     allApps = new ArrayList<>();
    private AccessibilityPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = AccessibilityPreferences.get(this);

        RecyclerView rvApps    = findViewById(R.id.rv_apps);
        SearchView   searchView = findViewById(R.id.search_view);

        adapter       = new AppGridAdapter(this);
        layoutManager = new GridLayoutManager(this, prefs.getGridCols());

        rvApps.setLayoutManager(layoutManager);
        rvApps.setAdapter(adapter);
        rvApps.setHasFixedSize(false);

        adapter.setOnAppClickListener(this::launchApp);

        loadInstalledApps();

        // Search filter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { return false; }
            @Override public boolean onQueryTextChange(String q) {
                filterApps(q);
                return true;
            }
        });

        // FAB → open Accessibility Settings (MainActivity4)
        findViewById(R.id.fab_settings).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity4.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register to hear pref changes while this screen is visible
        prefs.setListener(this);
        // Refresh in case settings changed while we were in MainActivity4
        onPrefsChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.clearListener();
    }

    // ── AccessibilityPreferences.OnPrefsChangedListener ───────────
    @Override
    public void onPrefsChanged() {
        // Update grid column count
        int cols = prefs.getGridCols();
        if (layoutManager.getSpanCount() != cols) {
            layoutManager.setSpanCount(cols);
        }
        // Refresh all cells (font + icon sizes)
        adapter.notifyResized();
    }

    // ── Back button: HOME screen never goes back ───────────────────
    @Override
    public void onBackPressed() {
        // intentionally empty — home screen has nowhere to go back to
    }

    // ── Load apps ─────────────────────────────────────────────────
    private void loadInstalledApps() {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolved = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolved, new ResolveInfo.DisplayNameComparator(pm));

        allApps.clear();
        for (ResolveInfo ri : resolved) {
            String pkg = ri.activityInfo.packageName;
            if (pkg.equals(getPackageName())) continue; // skip Adapto itself
            allApps.add(new AppInfo(
                    ri.loadLabel(pm).toString(),
                    pkg,
                    ri.activityInfo.name,
                    ri.loadIcon(pm)
            ));
        }
        adapter.setApps(allApps);
    }

    private void filterApps(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.setApps(allApps);
            return;
        }
        String lower = query.toLowerCase().trim();
        List<AppInfo> filtered = new ArrayList<>();
        for (AppInfo a : allApps) {
            if (a.label.toLowerCase().contains(lower)) filtered.add(a);
        }
        adapter.setApps(filtered);
    }

    private void launchApp(AppInfo app) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(app.packageName, app.activityName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            // App may have been uninstalled — reload the list
            loadInstalledApps();
        }
    }
}
