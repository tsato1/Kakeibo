package com.kakeibo.ui.adapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.SubApp
import com.kakeibo.data.CategoryStatus
import com.kakeibo.ui.view.AmountTextWatcher
import com.kakeibo.ui.model.SearchCriteriaCard
import com.kakeibo.util.UtilDate
import java.text.SimpleDateFormat
import java.util.*

internal class SearchRecyclerViewAdapter(private val _lstSearchCriteriaCards: ArrayList<SearchCriteriaCard>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val _categoryStatusList: LiveData<List<CategoryStatus>>? = null

    /*** date range card  */
    internal inner class ViewHolderDateRange(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: FrameLayout
        var cardView: CardView
        var btnFrom: Button
        var btnTo: Button

        internal inner class ButtonClickListener : View.OnClickListener {
            override fun onClick(view: View) {
                when (view.id) {
                    R.id.btn_date_from -> showYMDPickerDialog(btnFrom)
                    R.id.btn_date_to -> showYMDPickerDialog(btnTo)
                }
            }
        }

        private fun showYMDPickerDialog(button: Button) {
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH] + 1
            val day = cal[Calendar.DAY_OF_MONTH]
            val dialog = DatePickerDialog(itemView.context, { picker, year, month, day ->
                val cal = GregorianCalendar(year, month, day)
                val date = cal.time
                val str = SimpleDateFormat(UtilDate.DATE_FORMATS[_dateFormat],
                        Locale.getDefault()).format(date)
                button.text = str
            }, year, month - 1, day)
            dialog.show()
        }

        init {
            layout = itemView.findViewById(R.id.frl_card_date_range)
            cardView = itemView.findViewById(R.id.cdv_date_range)
            btnFrom = itemView.findViewById(R.id.btn_date_from)
            btnTo = itemView.findViewById(R.id.btn_date_to)
            btnFrom.setOnClickListener(ButtonClickListener())
            btnTo.setOnClickListener(ButtonClickListener())
        }
    }

    /*** amount range card  */
    internal inner class ViewHolderAmountRange(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: FrameLayout
        var cardView: CardView
        var edtMin: EditText
        var edtMax: EditText

        init {
            layout = itemView.findViewById(R.id.frl_card_amount_range)
            cardView = itemView.findViewById(R.id.cdv_amount_range)
            edtMin = itemView.findViewById(R.id.edt_amount_min)
            edtMax = itemView.findViewById(R.id.edt_amount_max)
            edtMin.addTextChangedListener(AmountTextWatcher(edtMin))
            edtMax.addTextChangedListener(AmountTextWatcher(edtMax))
        }
    }

    /*** category card  */
    internal inner class ViewHolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: FrameLayout
        var cardView: CardView
        var btnCategory: Button
        var selectedCategoryCode = 0

        init {
            layout = itemView.findViewById(R.id.frl_card_category)
            cardView = itemView.findViewById(R.id.cdv_category)
            btnCategory = itemView.findViewById(R.id.btn_card_category)
            btnCategory.setOnClickListener { view: View? ->
                //_categoryStatusList = UtilCategory.getDspCategoryList(_context);
                val adapter = CategoryListAdapter(
                        itemView.context, 0, ArrayList())
                val builder = AlertDialog.Builder(itemView.context)
                val inflater = itemView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val convertView = inflater.inflate(R.layout.dialog_bas_search_category, null)
                builder.setView(convertView)
                builder.setCancelable(true)
                builder.setIcon(R.mipmap.ic_mikan)
                builder.setTitle(R.string.category)
                builder.setNegativeButton(R.string.cancel) { dialog: DialogInterface?, which: Int -> }
                val lv = convertView.findViewById<ListView>(R.id.lsv_base_search_category)
                lv.adapter = adapter
                val dialog: Dialog = builder.show()
                lv.onItemClickListener = AdapterView.OnItemClickListener { parent: AdapterView<*>?, v: View?, pos: Int, id: Long -> }
            }
        }
    }

    /*** memo card  */
    inner class ViewHolderMemo internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: FrameLayout
        var cardView: CardView
        var edtMemo: EditText

        init {
            layout = itemView.findViewById(R.id.frl_card_memo)
            cardView = itemView.findViewById(R.id.cdv_memo)
            edtMemo = itemView.findViewById(R.id.edt_card_memo)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (_lstSearchCriteriaCards[position].type) {
            0 -> SearchCriteriaCard.TYPE_DATE_RANGE
            1 -> SearchCriteriaCard.TYPE_AMOUNT_RANGE
            2 -> SearchCriteriaCard.TYPE_CATEGORY
            3 -> SearchCriteriaCard.TYPE_MEMO
            else -> -1
        }
    }

    override fun getItemCount(): Int {
        return _lstSearchCriteriaCards.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            SearchCriteriaCard.TYPE_DATE_RANGE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_date_range, parent, false)
                ViewHolderDateRange(view)
            }
            SearchCriteriaCard.TYPE_AMOUNT_RANGE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_amount_range, parent, false)
                ViewHolderAmountRange(view)
            }
            SearchCriteriaCard.TYPE_CATEGORY -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_category, parent, false)
                ViewHolderCategory(view)
            }
            SearchCriteriaCard.TYPE_MEMO -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_memo, parent, false)
                ViewHolderMemo(view)
            }
            else -> { // Date Range
                view = LayoutInflater.from(parent.context).inflate(R.layout.card_date_range, parent, false)
                ViewHolderDateRange(view)
            }
        }
    }

    fun removeItem(position: Int) {
        Log.d(TAG, "removeItem() called")
        _lstSearchCriteriaCards.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(searchCriteriaCard: SearchCriteriaCard, position: Int) {
        _lstSearchCriteriaCards.add(position, searchCriteriaCard)
        notifyItemInserted(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val `object` = _lstSearchCriteriaCards[position] ?: return
        when (`object`.type) {
            SearchCriteriaCard.TYPE_DATE_RANGE -> {
                var  viewHolderDateRange: ViewHolderDateRange? = holder as ViewHolderDateRange
            }
            SearchCriteriaCard.TYPE_AMOUNT_RANGE -> {
                var viewHolderAmountRange: ViewHolderAmountRange? = holder as ViewHolderAmountRange
            }
            SearchCriteriaCard.TYPE_CATEGORY -> {
                var viewHolderCategory: ViewHolderCategory? = holder as ViewHolderCategory
            }
            SearchCriteriaCard.TYPE_MEMO -> {
                var viewHolderMemo: ViewHolderMemo? = holder as ViewHolderMemo
            }
        }
    }

    companion object {
        private val TAG = SearchRecyclerViewAdapter::class.java.simpleName
        private var _dateFormat: Int = 0
    }

    init {
        _dateFormat = SubApp.getDateFormat(R.string.pref_key_date_format)
        //        _categoryStatusList = ((SubApp) context).getRepository().getDspCategories();
    }
}