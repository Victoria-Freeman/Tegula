package com.venddair.tegula.core;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class TegulaEdgeToEdge {

    /**
     * Enables edge-to-edge display: transparent bars, full-screen layout,
     * and automatic content inset padding. One call, no additional setup.
     */
    public static void setupEdgeToEdge(Activity activity) {
        Window window = activity.getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        boolean isLight = isLightBackground(activity);
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if (isLight && Build.VERSION.SDK_INT >= 23) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (isLight && Build.VERSION.SDK_INT >= 26) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flags);

        applyInsetsToContentView(activity);
    }

    private static void applyInsetsToContentView(Activity activity) {
        ViewGroup content = activity.findViewById(android.R.id.content);
        if (content == null) return;

        content.setOnApplyWindowInsetsListener((view, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), bottom);
            return insets.consumeSystemWindowInsets();
        });
        content.requestApplyInsets();
    }

    private static boolean isLightBackground(Activity activity) {
        int color = Build.VERSION.SDK_INT >= 23
                ? activity.getResources().getColor(
                        com.venddair.tegula.colors.R.color.tegula_bg_root, activity.getTheme())
                : activity.getResources().getColor(
                        com.venddair.tegula.colors.R.color.tegula_bg_root);
        int luminance = (int) (0.299 * Color.red(color)
                + 0.587 * Color.green(color) + 0.114 * Color.blue(color));
        return luminance > 128;
    }
}
