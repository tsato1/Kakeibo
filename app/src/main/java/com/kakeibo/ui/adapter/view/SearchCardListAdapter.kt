package com.kakeibo.ui.adapter.view

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.kakeibo.R
import com.kakeibo.data.Category
import com.kakeibo.ui.MainActivity
import com.kakeibo.ui.view.AmountTextWatcher
import com.kakeibo.ui.model.SearchCriteriaCard
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import java.text.SimpleDateFormat
import java.util.*

internal class SearchCardListAdapter(
        private val _lstSearchCriteriaCards: ArrayList<SearchCriteriaCard>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var _allCategoryList: List<Category>

    fun setAllCategoryList(allCategoryList: List<Category>) {
        _allCategoryList = allCategoryList
    }

    /* date range card  */
    internal inner class ViewHolderDateRange(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: FrameLayout = itemView.findViewById(R.id.frl_card_date_range)
        var cardView: CardView = itemView.findViewById(R.id.cdv_date_range)
        var btnFrom: Button = itemView.findViewById(R.id.btn_date_from)
        var btnTo: Button = itemView.findViewById(R.id.btn_date_to)

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
            val dialog = DatePickerDialog(itemView.context, { _, y, m, d ->
                val gCal = GregorianCalendar(y, m, d)
                val date = gCal.time
                val str = SimpleDateFormat(UtilDate.DATE_FORMATS[MainActivity.dateFormat],
                        Locale.getDefault()).format(date)
                button.text = str
            }, year, month - 1, day)
            dialog.show()
        }

        init {
            btnFrom.setOnClickListener(ButtonClickListener())
            btnTo.setOnClickListener(ButtonClickListener())
        }
    }

    /* amount range card  */
    internal inner class ViewHolderAmountRange(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: FrameLayout = itemView.findViewById(R.id.frl_card_amount_range)
        var cardView: CardView = itemView.findViewById(R.id.cdv_amount_range)
        var edtMin: EditText = itemView.findViewById(R.id.edt_amount_min)
        var edtMax: EditText = itemView.findViewById(R.id.edt_amount_max)

        init {
            edtMin.addTextChangedListener(AmountTextWatcher(edtMin))
            edtMax.addTextChangedListener(AmountTextWatcher(edtMax))
        }
    }

    /* category card  */
    internal inner class ViewHolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: FrameLayout = itemView.findViewById(R.id.frl_card_category)
        var cardView: CardView = itemView.findViewById(R.id.cdv_category)
        var btnCategory: Button = itemView.findViewById(R.id.btn_card_category)
        var selectedCategoryCode = 0

        init {
            btnCategory.setOnClickListener {
                val list = _allCategoryList
                val adapter = CategoryListAdapter(itemView.context, 0, list)
                val builder = AlertDialog.Builder(itemView.context)
                val inflater = itemView.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val convertView = inflater.inflate(R.layout.dialog_bas_search_category, null)
                builder.setView(convertView)
                builder.setCancelable(true)
                builder.setIcon(R.mipmap.ic_mikan)
                builder.setTitle(R.string.category)
                builder.setNegativeButton(R.string.cancel) { _, _ -> }
                val lv = convertView.findViewById<ListView>(R.id.lsv_base_search_category)
                lv.adapter = adapter
                val dialog = builder.show()
                lv.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos: Int, _->
                    selectedCategoryCode = list[pos].code
                    btnCategory.text = if (selectedCategoryCode < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                        convertView.resources.getStringArray(R.array.default_category)[selectedCategoryCode]
                    }
                    else {
                        list[pos].name
                    }
                    dialog.dismiss()
                }
            }
        }
    }

    /* memo card */
    inner class ViewHolderMemo internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var layout: FrameLayout = itemView.findViewById(R.id.frl_card_memo)
        var cardView: CardView = itemView.findViewById(R.id.cdv_memo)
        var edtMemo: EditText = itemView.findViewById(R.id.edt_card_memo)
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
        _lstSearchCriteriaCards.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(searchCriteriaCard: SearchCriteriaCard, position: Int) {
        _lstSearchCriteriaCards.add(position, searchCriteriaCard)
        notifyItemInserted(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = _lstSearchCriteriaCards[position]
        when (card.type) {
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
}