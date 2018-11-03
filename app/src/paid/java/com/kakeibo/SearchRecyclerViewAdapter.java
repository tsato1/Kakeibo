package com.kakeibo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.kakeibo.util.UtilDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final static String TAG = SearchRecyclerViewAdapter.class.getSimpleName();

    private static String[] mCategories;

    private Context _context;
    private ArrayList<Card> _lstCards;

    SearchRecyclerViewAdapter(Context context, ArrayList<Card> lstCards) {
        _context = context;
        _lstCards = lstCards;

        mCategories = _context.getResources().getStringArray(R.array.default_category);
    }

    /*** date range card ***/
    class ViewHolderDateRange extends RecyclerView.ViewHolder {
        FrameLayout layout;
        CardView cardView;
        Button btnFrom, btnTo;

        ViewHolderDateRange(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.frl_card_date_range);
            cardView = itemView.findViewById(R.id.cdv_date_range);
            btnFrom = itemView.findViewById(R.id.btn_date_from);
            btnTo = itemView.findViewById(R.id.btn_date_to);

            btnFrom.setOnClickListener(new ButtonClickListener());
            btnTo.setOnClickListener(new ButtonClickListener());
        }

        class ButtonClickListener implements View.OnClickListener {
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_date_from:
                        showYMDPickerDialog(btnFrom);
                        break;
                    case R.id.btn_date_to:
                        showYMDPickerDialog(btnTo);
                        break;
                }
            }
        }

        private void showYMDPickerDialog(final Button button) {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(_context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker picker, int year, int month, int day){
                    GregorianCalendar cal = new GregorianCalendar(year, month, day);
                    Date date = cal.getTime();
                    String str = new SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.sDateFormat],
                            Locale.getDefault()).format(date);
                    button.setText(str);
                }
            }, year, month-1, day);
            dialog.show();
        }
    }

    /*** amount range card ***/
    class ViewHolderAmountRange extends RecyclerView.ViewHolder {
        FrameLayout layout;
        CardView cardView;
        EditText edtMin, edtMax;

        ViewHolderAmountRange(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.frl_card_amount_range);
            cardView = itemView.findViewById(R.id.cdv_amount_range);
            edtMin = itemView.findViewById((R.id.edt_amount_min));
            edtMax = itemView.findViewById(R.id.edt_amount_max);

            edtMin.addTextChangedListener(new AmountTextWatcher(edtMin, MainActivity.sCurrency.getDefaultFractionDigits()));
            edtMax.addTextChangedListener(new AmountTextWatcher(edtMax, MainActivity.sCurrency.getDefaultFractionDigits()));
        }
    }

    /*** currency card ***/
    class ViewHolderCurrency extends RecyclerView.ViewHolder {
        FrameLayout layout;
        CardView cardView;
        Button btnCurrency;

        ViewHolderCurrency(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.frl_card_currency);
            cardView = itemView.findViewById(R.id.cdv_currency);
            btnCurrency = itemView.findViewById(R.id.btn_card_currency);
            btnCurrency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(_context)
                            .setSingleChoiceItems(MainActivity.arrCurrencyCodes, 0, null)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    String selectedItem = ((AlertDialog) dialog).getListView().getSelectedItem().toString();
                                    btnCurrency.setText(selectedItem);
                                }
                            })
                            .show();
                }
            });
        }
    }

    /*** category card ***/
    class ViewHolderCategory extends RecyclerView.ViewHolder {
        FrameLayout layout;
        CardView cardView;
        Button btnCategory;
        int selectedPosition=0;// todo need map between category code and selectedposition

        ViewHolderCategory(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.frl_card_category);
            cardView = itemView.findViewById(R.id.cdv_category);
            btnCategory = itemView.findViewById(R.id.btn_card_category);
            btnCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(_context)
                            .setSingleChoiceItems(mCategories, 0, null)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                    btnCategory.setText(mCategories[selectedPosition]);
                                }
                            })
                            .show();
                }
            });
        }

        int getSelectedPosition() {
            return selectedPosition;
        }
    }

    /*** memo card ***/
    public class ViewHolderMemo extends RecyclerView.ViewHolder {
        FrameLayout layout;
        CardView cardView;
        EditText edtMemo;

        ViewHolderMemo(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.frl_card_memo);
            cardView = itemView.findViewById(R.id.cdv_memo);
            edtMemo = itemView.findViewById(R.id.edt_card_memo);
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch(_lstCards.get(position).type) {
            case 0:
                return Card.TYPE_DATE_RANGE;
            case 1:
                return Card.TYPE_AMOUNT_RANGE;
            case 2:
                return Card.TYPE_CURRENCY;
            case 3:
                return Card.TYPE_CATEGORY;
            case 4:
                return Card.TYPE_MEMO;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return _lstCards.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Card.TYPE_DATE_RANGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_date_range, parent, false);
                return new ViewHolderDateRange(view);
            case Card.TYPE_AMOUNT_RANGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_amount_range, parent, false);
                return new ViewHolderAmountRange(view);
            case Card.TYPE_CURRENCY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_currency, parent, false);
                return new ViewHolderCurrency(view);
            case Card.TYPE_CATEGORY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category, parent, false);
                return new ViewHolderCategory(view);
            case Card.TYPE_MEMO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_memo, parent, false);
                return new ViewHolderMemo(view);
            default:
                return null;
        }
    }

    void removeItem(int position) {
        Log.d(TAG, "removeItem() called");
        _lstCards.remove(position);
        notifyItemRemoved(position);
    }

    void restoreItem(Card card, int position) {
        _lstCards.add(position, card);
        notifyItemInserted(position);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        Card object = _lstCards.get(position);

        if (object == null) return;

        switch (object.type) {
            case Card.TYPE_DATE_RANGE:
                ViewHolderDateRange viewHolderDateRange = (ViewHolderDateRange)holder;
                break;
            case Card.TYPE_AMOUNT_RANGE:
                ViewHolderAmountRange viewHolderAmountRange = (ViewHolderAmountRange)holder;
                break;
            case Card.TYPE_CURRENCY:
                ViewHolderCurrency viewHolderCurrency = (ViewHolderCurrency)holder;
                break;
            case Card.TYPE_CATEGORY:
                ViewHolderCategory viewHolderCategory = (ViewHolderCategory)holder;
                break;
            case Card.TYPE_MEMO:
                ViewHolderMemo viewHolderMemo = (ViewHolderMemo)holder;
                viewHolderMemo.edtMemo.getText().clear();
                break;
        }
    }
}
