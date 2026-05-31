package com.venddair.tegula.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TegulaDialog {

    public static class Item {
        public final int iconRes;
        public final String text;

        public Item(int iconRes, String text) {
            this.iconRes = iconRes;
            this.text = text;
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int index, String text);
    }

    public static AlertDialog show(Context context, String title, Item[] items,
                                   OnItemSelectedListener listener) {
        View view = View.inflate(context, R.layout.tegula_dialog, null);

        TextView titleView = view.findViewById(R.id.tegula_dialog_title);
        titleView.setText(title);

        LinearLayout rows = view.findViewById(R.id.tegula_dialog_rows);
        int sm = dimenPx(context, com.venddair.tegula.dimens.R.dimen.tegula_spacing_sm);
        int lg = dimenPx(context, com.venddair.tegula.dimens.R.dimen.tegula_spacing_lg);
        int touchTarget = dimenPx(context, com.venddair.tegula.dimens.R.dimen.tegula_touch_target);
        float textSizeSp = context.getResources().getDimension(
                com.venddair.tegula.dimens.R.dimen.tegula_text_sm)
                / context.getResources().getDisplayMetrics().scaledDensity;
        int textColor = color(context, com.venddair.tegula.colors.R.color.tegula_text_on_accent);
        int iconColor = color(context, com.venddair.tegula.colors.R.color.tegula_text_on_accent);

        for (int i = 0; i < items.length; i++) {
            final int idx = i;
            final String text = items[i].text;

            LinearLayout row = new LinearLayout(context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.tegula_dialog_row_bg);
            row.setPadding(lg, 0, lg, 0);
            row.setMinimumHeight(touchTarget);
            row.setClickable(true);
            row.setFocusable(true);

            ImageView icon = new ImageView(context);
            icon.setImageResource(items[i].iconRes);
            icon.setColorFilter(iconColor);
            int iconSize = dimenPx(context, com.venddair.tegula.dimens.R.dimen.tegula_spacing_xl);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
            iconParams.setMarginEnd(lg);
            row.addView(icon, iconParams);

            TextView label = new TextView(context);
            label.setText(text);
            label.setTextColor(textColor);
            label.setTextSize(textSizeSp);
            row.addView(label, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            row.setOnClickListener(v -> {
                if (listener != null) listener.onItemSelected(idx, text);
                AlertDialog dlg = (AlertDialog) v.getTag();
                if (dlg != null) dlg.dismiss();
            });

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) rowParams.topMargin = sm;
            rows.addView(row, rowParams);
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            int margin = dimenPx(context, com.venddair.tegula.dimens.R.dimen.tegula_spacing_xl);
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            window.setLayout(screenWidth - margin * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        for (int i = 0; i < rows.getChildCount(); i++) {
            rows.getChildAt(i).setTag(dialog);
        }

        return dialog;
    }

    @SuppressWarnings("deprecation")
    private static int color(Context c, int id) {
        return Build.VERSION.SDK_INT >= 23
                ? c.getResources().getColor(id, c.getTheme())
                : c.getResources().getColor(id);
    }

    private static int dimenPx(Context c, int id) {
        return c.getResources().getDimensionPixelSize(id);
    }
}
