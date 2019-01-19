package com.devnock.basearchitecture.utils.view_utils;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.widget.EditText;

import com.emjwho.app.R;
import com.emjwho.app.application.App;

public class TextViewUtils {

    public static void listenErrorWatcher(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new EditTextErrorTextWatcher(layout));
    }

    public static void showError(TextInputLayout textInputLayout, int stringId) {
        textInputLayout.setError(App.getContext().getString(stringId));
        textInputLayout.setErrorEnabled(true);
    }

    public interface OnTextChangeListener {
        void onTextChanged(CharSequence text);
    }

    public static void listenTextChanging(EditText editText, OnTextChangeListener onTextChangeListener) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onTextChangeListener.onTextChanged(editable);
            }
        });
    }

    public static void setupDefaultFontSize(TextInputLayout textInputLayout, TextInputEditText textInputEditText) {
        textInputLayout.setTypeface(ResourcesCompat.getFont(App.getContext(), R.font.poppins_regular));
        textInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int start, int before, int count) {
                if (arg0.length() == 0) {
                    textInputEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, App.getContext().getResources().getDimension(R.dimen.authorization_edit_text_size_hint));
                    textInputEditText.setTextColor(ContextCompat.getColor(App.getContext(), R.color.authorization_hint_text));
                } else {
                    textInputEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, App.getContext().getResources().getDimensionPixelOffset(R.dimen.authorization_edit_text_size));
                    textInputEditText.setTextColor(ContextCompat.getColor(App.getContext(), R.color.chocolate_brown));
                }
            }
        });
    }

}
