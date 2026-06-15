package com.tejyash.myadapto;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Drives the home-screen app grid.
 * Call notifyResized() after changing font/icon prefs to update all cells instantly.
 */
public class AppGridAdapter extends RecyclerView.Adapter<AppGridAdapter.AppViewHolder> {

    public interface OnAppClickListener {
        void onAppClick(AppInfo app);
    }

    private final List<AppInfo>    apps = new ArrayList<>();
    private OnAppClickListener     clickListener;
    private final AccessibilityPreferences prefs;

    public AppGridAdapter(Context ctx) {
        this.prefs = AccessibilityPreferences.get(ctx);
    }

    public void setOnAppClickListener(OnAppClickListener l) { this.clickListener = l; }

    public void setApps(List<AppInfo> newApps) {
        apps.clear();
        apps.addAll(newApps);
        notifyDataSetChanged();
    }

    /** Call this whenever font or icon size prefs change — refreshes all visible cells. */
    public void notifyResized() {
        notifyItemRangeChanged(0, apps.size());
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app_grid, parent, false);
        return new AppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.bind(apps.get(position), prefs.getFontSizeSp(), prefs.getIconSizeDp());
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onAppClick(apps.get(position));
        });
    }

    @Override
    public int getItemCount() { return apps.size(); }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivIcon;
        final TextView  tvLabel;

        AppViewHolder(View itemView) {
            super(itemView);
            ivIcon  = itemView.findViewById(R.id.iv_app_icon);
            tvLabel = itemView.findViewById(R.id.tv_app_label);
        }

        void bind(AppInfo app, float fontSp, int iconDp) {
            tvLabel.setText(app.label);
            ivIcon.setImageDrawable(app.icon);
            tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSp);

            int px = dpToPx(itemView.getContext(), iconDp);
            ViewGroup.LayoutParams lp = ivIcon.getLayoutParams();
            lp.width  = px;
            lp.height = px;
            ivIcon.setLayoutParams(lp);
        }

        private static int dpToPx(Context ctx, int dp) {
            return Math.round(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, dp,
                    ctx.getResources().getDisplayMetrics()));
        }
    }
}
