package com.devnock.basearchitecture.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.devnock.basearchitecture.R;

public class LoadingDialog extends Dialog {

    public static LoadingDialog show(Context context) {
        return show(context, R.style.LoadingDialog_Dim, false, null);
    }

    public static LoadingDialog show(Context context, int theme) {
        return show(context, theme, false, null);
    }

    public static LoadingDialog show(Context context, int theme, boolean cancelable) {
        return show(context, theme, cancelable, null);
    }

    public static LoadingDialog show(Context context, int theme,
                                     boolean cancelable, OnCancelListener cancelListener) {
        LoadingDialog dialog = new LoadingDialog(context, theme);
        dialog.setTitle(null);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.addContentView(new ProgressBar(context), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    private LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

}
