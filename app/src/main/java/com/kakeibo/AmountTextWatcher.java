package com.kakeibo;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

public class AmountTextWatcher implements TextWatcher {
    private EditText mEdtAmount;
    private int mFractionDigits;

    public AmountTextWatcher(EditText edtAmount, int fractionDigits) {
        mEdtAmount = edtAmount;
        mFractionDigits = fractionDigits;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable editable) {
        String str = editable.toString();
        int length = str.length();

        if (mFractionDigits == 0) {
            if (length > 1 && str.charAt(length - 1) == '.') {
                mEdtAmount.setText(str.substring(0, length - 1));
                mEdtAmount.setSelection(mEdtAmount.getText().length());
            }
            return;
        }

        if (length == 1) {
            if (str.charAt(0) == '.') {
                mEdtAmount.setText("");
            }
        } else if (length > 1) {
            if (str.charAt(0) == '0' && str.charAt(1) != '.') {
                mEdtAmount.setText(str.substring(0, length - 1));
                mEdtAmount.setSelection(mEdtAmount.getText().length());
                return;
            }

            if (str.charAt(length - 1) == '.' && secondTime(str)) {
                mEdtAmount.setText(str.substring(0, length - 1));
                mEdtAmount.setSelection(mEdtAmount.getText().length());
                return;
            }

            if (str.contains(".") &&
                    str.substring(str.indexOf('.')).length() > mFractionDigits + 1) {
                mEdtAmount.setText(str.substring(0, length - 1));
                mEdtAmount.setSelection(mEdtAmount.getText().length());
            }
        }
    }

    private boolean secondTime(String str) {
        Set<Character> set = new HashSet<>();

        for (int i = 0; i < str.length(); ++i) {
            if (set.contains(str.charAt(i)) && str.charAt(i)=='.') {
                return true;
            }
            set.add(str.charAt(i));
        }
        return false;
    }
}
