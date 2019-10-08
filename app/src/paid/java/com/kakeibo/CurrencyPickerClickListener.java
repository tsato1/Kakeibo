package com.kakeibo;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kakeibo.util.UtilCurrency;

import java.util.Currency;

public class CurrencyPickerClickListener implements View.OnClickListener {
    private Activity _activity;
    private Button btnCurrency;
    private EditText edtAmount;

    public CurrencyPickerClickListener (Activity activity, Button btnCurrency, EditText edtAmount) {
        this._activity = activity;
        this.btnCurrency = btnCurrency;
        this.edtAmount = edtAmount;
    }

    public void  onClick(View view) {
        //todo
//        String[] arrCurrency = UtilCurrency.arlCurrencyCode.toArray(new String[UtilCurrency.sAllCurrencyLength]);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
//        builder.setTitle(_activity.getString(R.string.pref_title_currency))
//                .setIcon(R.mipmap.ic_mikan)
//                .setSingleChoiceItems(arrCurrency, UtilCurrency.intCurrencyIndex, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String pickedCurrencyCode = UtilCurrency.arlCurrencyCode.get(which);
//                        btnCurrency.setText(pickedCurrencyCode);
//
//                        if (!pickedCurrencyCode.equals(MainActivity.sCurrency.getCurrencyCode())) {
//                            edtAmount.setText("");
//                            int digits = 0;
//                            if (!pickedCurrencyCode.equals(UtilCurrency.CURRENCY_NONE)) {
//                                digits = Currency.getInstance(pickedCurrencyCode).getDefaultFractionDigits();
//                            }
//                            edtAmount.addTextChangedListener(new AmountTextWatcher(edtAmount, digits));
//                        }
//
//                        dialog.dismiss();
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.setCancelable(true);
//        dialog.show();
    }
}