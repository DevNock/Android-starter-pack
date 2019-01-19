package com.devnock.basearchitecture.utils;

import android.view.View;

public final class ViewUtils {
    public static void setVisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void setVisible(boolean visible, View... views) {
        for (final View view : views) {
            setVisible(view, visible);
        }
    }

    public static void show(View... views) {
        for (final View view : views) {
            setVisible(view, true);
        }
    }

    public static void hide(View... views) {
        for (final View view : views) {
            setVisible(view, false);
        }
    }

}
