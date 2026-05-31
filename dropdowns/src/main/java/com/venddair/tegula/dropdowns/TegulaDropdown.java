package com.venddair.tegula.dropdowns;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Floating dropdown menu powered by PopupWindow.
 * Items appear above the UI without affecting layout flow.
 */
public class TegulaDropdown {

    public interface OnItemSelectedListener {
        void onItemSelected(int index, String text);
    }

    /**
     * Populates the dropdown with items. Creates the PopupWindow content.
     */
    public static void setItems(View dropdownRoot, String[] items) {
        Context ctx = dropdownRoot.getContext();
        ScrollView scroll = buildContent(ctx, dropdownRoot, items);

        // Measure content to calculate height before showing
        int widthSpec = View.MeasureSpec.makeMeasureSpec(
                dropdownRoot.getWidth() > 0 ? dropdownRoot.getWidth()
                        : View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.AT_MOST);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.makeMeasureSpec(5000, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.AT_MOST);
        scroll.measure(widthSpec, heightSpec);
        int contentHeight = scroll.getMeasuredHeight();

        // Clamp height to ~7 items
        int maxPx = getDimenPx(ctx, com.venddair.tegula.dimens.R.dimen.tegula_dropdown_max_height);
        int popupHeight = Math.min(contentHeight, maxPx);

        PopupWindow popup = new PopupWindow(scroll,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                popupHeight, true);
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.setOutsideTouchable(true);
        popup.setOnDismissListener(() -> rotateChevron(dropdownRoot, 0));

        dropdownRoot.setTag(R.id.tegula_dropdown_popup, popup);
        dropdownRoot.setTag(R.id.tegula_dropdown_items, items);
    }

    /**
     * Sets a listener called when an item is tapped.
     */
    public static void setOnItemSelectedListener(View dropdownRoot, OnItemSelectedListener listener) {
        dropdownRoot.setTag(R.id.tegula_dropdown_listener, listener);
    }

    /**
     * Toggles the popup open/closed, anchored below the trigger.
     */
    public static void toggle(View dropdownRoot) {
        PopupWindow popup = getPopup(dropdownRoot);
        if (popup == null) return;

        if (popup.isShowing()) {
            popup.dismiss();
            rotateChevron(dropdownRoot, 0);
        } else {
//            popup.setWidth(dropdownRoot.getWidth());
            popup.showAsDropDown(dropdownRoot);
            rotateChevron(dropdownRoot, 180);
        }
    }

    /** Shows the popup. */
    public static void show(View dropdownRoot) {
        PopupWindow popup = getPopup(dropdownRoot);
        if (popup != null && !popup.isShowing()) {
//            popup.setWidth(dropdownRoot.getWidth());
            popup.showAsDropDown(dropdownRoot);
            rotateChevron(dropdownRoot, 180);
        }
    }

    /** Hides the popup. */
    public static void hide(View dropdownRoot) {
        PopupWindow popup = getPopup(dropdownRoot);
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
            rotateChevron(dropdownRoot, 0);
        }
    }

    /** Updates the trigger label to show the selected item's text. */
    public static void setSelected(View dropdownRoot, int index) {
        TextView label = dropdownRoot.findViewById(R.id.tegula_dropdown_label);
        String[] items = getItems(dropdownRoot);
        if (label == null || items == null || index < 0 || index >= items.length) return;

        label.setText(items[index]);
        int white = getThemeColor(dropdownRoot,
                com.venddair.tegula.colors.R.color.tegula_text_on_accent);
        label.setTextColor(white);
        hide(dropdownRoot);
    }

    /** Returns true if the popup is currently showing. */
    public static boolean isShowing(View dropdownRoot) {
        PopupWindow popup = getPopup(dropdownRoot);
        return popup != null && popup.isShowing();
    }

    // --- Private ---

    private static ScrollView buildContent(Context ctx, View dropdownRoot, String[] items) {
        int white = ctx.getResources().getColor(
                com.venddair.tegula.colors.R.color.tegula_text_on_accent);

        LinearLayout container = new LinearLayout(ctx);
        container.setOrientation(LinearLayout.VERTICAL);
        int xs = getDimenPx(ctx, com.venddair.tegula.dimens.R.dimen.tegula_spacing_xs);
        int lg = getDimenPx(ctx, com.venddair.tegula.dimens.R.dimen.tegula_spacing_lg);
        int touchTarget = getDimenPx(ctx, com.venddair.tegula.dimens.R.dimen.tegula_touch_target);
        float textSizeSp = ctx.getResources().getDimension(
                com.venddair.tegula.dimens.R.dimen.tegula_text_sm)
                / ctx.getResources().getDisplayMetrics().scaledDensity;
        container.setPadding(xs, xs, xs, xs);

        for (int i = 0; i < items.length; i++) {
            final int idx = i;
            final String text = items[i];
            TextView tv = new TextView(ctx);
            tv.setText(text);
            tv.setTextColor(white);
            tv.setTextSize(textSizeSp);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setBackgroundResource(R.drawable.tegula_dropdown_item_bg);
            tv.setPadding(lg, 0, lg, 0);
            tv.setMinimumHeight(touchTarget);
            tv.setClickable(true);
            tv.setFocusable(true);

            tv.setOnClickListener(v -> {
                OnItemSelectedListener listener = getListener(dropdownRoot);
                if (listener != null) listener.onItemSelected(idx, text);
            });

            container.addView(tv, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        ScrollView scroll = new ScrollView(ctx);
        scroll.setBackgroundResource(R.drawable.tegula_dropdown_bg);
        scroll.setClipToOutline(true);
        scroll.addView(container, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return scroll;
    }

    private static PopupWindow getPopup(View dropdownRoot) {
        Object tag = dropdownRoot.getTag(R.id.tegula_dropdown_popup);
        return tag instanceof PopupWindow ? (PopupWindow) tag : null;
    }

    private static String[] getItems(View dropdownRoot) {
        Object tag = dropdownRoot.getTag(R.id.tegula_dropdown_items);
        return tag instanceof String[] ? (String[]) tag : null;
    }

    private static OnItemSelectedListener getListener(View dropdownRoot) {
        Object tag = dropdownRoot.getTag(R.id.tegula_dropdown_listener);
        return tag instanceof OnItemSelectedListener ? (OnItemSelectedListener) tag : null;
    }

    private static void rotateChevron(View dropdownRoot, float degrees) {
        ImageView chevron = dropdownRoot.findViewById(R.id.tegula_dropdown_chevron);
        if (chevron != null) chevron.setRotation(degrees);
    }

    @SuppressWarnings("deprecation")
    private static int getThemeColor(View view, int colorRes) {
        if (Build.VERSION.SDK_INT >= 23) {
            return view.getContext().getResources()
                    .getColor(colorRes, view.getContext().getTheme());
        }
        return view.getContext().getResources().getColor(colorRes);
    }

    private static int getDimenPx(Context ctx, int dimenRes) {
        return ctx.getResources().getDimensionPixelSize(dimenRes);
    }
}
