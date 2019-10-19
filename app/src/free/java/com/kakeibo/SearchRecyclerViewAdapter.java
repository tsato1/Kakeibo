package com.kakeibo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.kakeibo.db.CategoriesDBAdapter;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final static String TAG = SearchRecyclerViewAdapter.class.getSimpleName();

//    private static String[] mCategories;
    private static List<KkbCategory> kkbCategoriesList;

    private Context _context;
    private ArrayList<Card> _lstCards;

    SearchRecyclerViewAdapter(Context context, ArrayList<Card> lstCards) {
        _context = context;
        _lstCards = lstCards;
        //        mCategories = _context.getResources().getStringArray(R.array.default_category);
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

            edtMin.addTextChangedListener(new AmountTextWatcher(edtMin));
            edtMax.addTextChangedListener(new AmountTextWatcher(edtMax));
        }
    }

    /*** category card ***/
    class ViewHolderCategory extends RecyclerView.ViewHolder {
        FrameLayout layout;
        CardView cardView;
        Button btnCategory;
        int selectedCategoryCode =0;

        ViewHolderCategory(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.frl_card_category);
            cardView = itemView.findViewById(R.id.cdv_category);
            btnCategory = itemView.findViewById(R.id.btn_card_category);
            btnCategory.setOnClickListener((View view) -> {
                CategoriesDBAdapter categoriesDBAdapter = new CategoriesDBAdapter();
                categoriesDBAdapter.open();
                Cursor c = categoriesDBAdapter.getParentCategories();
                kkbCategoriesList = new ArrayList<>();
                /*** ordered by location ***/
                if (c!=null && c.moveToFirst()) {
                    do {
                        KkbCategory kkbCategory = new KkbCategory(
                                c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_CODE)),
                                c.getString(c.getColumnIndex(CategoriesDBAdapter.COL_NAME)),
                                c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_COLOR)),
                                c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_DRAWABLE)),
                                c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_LOCATION)),
                                c.getInt(c.getColumnIndex(CategoriesDBAdapter.COL_SUB_CATEGORIES)),
                                c.getString(c.getColumnIndex(CategoriesDBAdapter.COL_DESC)),
                                c.getString(c.getColumnIndex(CategoriesDBAdapter.COL_SAVED_DATE))
                        );
                        kkbCategoriesList.add(kkbCategory);
                    } while (c.moveToNext());
                }
                categoriesDBAdapter.close();

                SearchCardCategoryListAdapter adapter =
                        new SearchCardCategoryListAdapter(_context, 0, kkbCategoriesList);
                AlertDialog.Builder builder = new AlertDialog.Builder(_context);
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View convertView = inflater.inflate(R.layout.dialog_bas_search_category, null);
                builder.setView(convertView);
                builder.setCancelable(true);
                builder.setIcon(R.mipmap.ic_mikan);
                builder.setTitle(R.string.category);
                ListView lv = convertView.findViewById(R.id.lsv_base_search_category);
                lv.setAdapter(adapter);
                final Dialog dialog = builder.show();
                lv.setOnItemClickListener((AdapterView<?> parent, View v, int pos, long id) -> {
                    selectedCategoryCode = kkbCategoriesList.get(pos).getCode();
//      disposable!!!!!!!!!!!!!!!!!!!!!!!!!!!!!              btnCategory.setText(MainActivity.sCategories[selectedCategoryCode]);
                    btnCategory.setText(UtilCategory.getCategoryStrFromCode(_context, selectedCategoryCode));
                    dialog.dismiss();
                });
            });
        }

        int getSelectedCategoryCode() {
            return selectedCategoryCode;
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
                return Card.TYPE_CATEGORY;
            case 3:
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
