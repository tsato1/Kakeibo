package com.kakeibo.settings.category.edit

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.divyanshu.draw.widget.DrawView
import com.kakeibo.R
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDrawing

class CustomCategoryImageFragment : Fragment() {

    companion object {
        const val TAG_INT = 2

        fun newInstance(): CustomCategoryImageFragment {
            val fragment = CustomCategoryImageFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var _selectedThickness = 1

    private lateinit var _drawView: DrawView
    private lateinit var _btnBack: Button
    private lateinit var _btnDone: Button
    private lateinit var _imbClearCanvas: ImageButton
    private lateinit var _imbUndo: ImageButton
    private lateinit var _imbRedo: ImageButton
    private lateinit var _imbThickness: ImageButton
    private lateinit var _thicknessList: Array<String>

    private val _customCategoryViewModel: CustomCategoryViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings_custom_category_image, container, false)
        findViews(view)

        _drawView.setColor(ContextCompat.getColor(requireContext(), R.color.colorBackground))
        _drawView.setStrokeWidth(resources.getDimension(R.dimen.draw_view_thickness_medium))
        _thicknessList = resources.getStringArray(R.array.thickness)
        when (_customCategoryViewModel.color.value) {
            UtilCategory.CATEGORY_COLOR_INCOME -> {
                _drawView.background = ContextCompat.getDrawable(requireContext(),
                        R.drawable.new_category_background_income)
            }
            UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                _drawView.background = ContextCompat.getDrawable(requireContext(),
                        R.drawable.new_category_background_expense)
            }
        }
        if (_customCategoryViewModel.code.value != -1) {
            _drawView.background = UtilDrawing.bitmapToDrawalbe(requireContext(),
                    UtilDrawing.bytesToBitmap(_customCategoryViewModel.image.value!!))
        }

        return view
    }

    private fun findViews(view: View) {
        _btnBack = view.findViewById(R.id.btn_back)
        _btnDone = view.findViewById(R.id.btn_done)
        _imbClearCanvas = view.findViewById(R.id.imb_clear_canvas)
        _imbUndo = view.findViewById(R.id.imb_undo)
        _imbRedo = view.findViewById(R.id.imb_redo)
        _imbThickness = view.findViewById(R.id.imb_thickness)
        _drawView = view.findViewById(R.id.draw_view)
        _btnBack.setOnClickListener(ButtonClickListener())
        _btnDone.setOnClickListener(ButtonClickListener())
        _imbClearCanvas.setOnClickListener(ButtonClickListener())
        _imbUndo.setOnClickListener(ButtonClickListener())
        _imbRedo.setOnClickListener(ButtonClickListener())
        _imbThickness.setOnClickListener(ButtonClickListener())
    }

    internal inner class ButtonClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.imb_clear_canvas -> {
                    _drawView.clearCanvas()

                    when (_customCategoryViewModel.color.value) { // income
                        UtilCategory.CATEGORY_COLOR_INCOME -> {
                            _drawView.background = ContextCompat.getDrawable(requireContext(),
                                    R.drawable.new_category_background_income)
                        }
                        UtilCategory.CATEGORY_COLOR_EXPENSE -> { // expense
                            _drawView.background = ContextCompat.getDrawable(requireContext(),
                                    R.drawable.new_category_background_expense)
                        }
                    }
                }
                R.id.imb_undo -> _drawView.undo()
                R.id.imb_redo -> _drawView.redo()
                R.id.imb_thickness -> {
                    val dialog = AlertDialog.Builder(activity)
                    dialog.setIcon(R.mipmap.ic_mikan)
                    dialog.setTitle(R.string.stroke_thickness)
                    dialog.setSingleChoiceItems(_thicknessList, _selectedThickness) { d3: DialogInterface, position: Int ->
                        when (position) {
                            0 -> _drawView.setStrokeWidth(resources.getDimension(R.dimen.draw_view_thickness_small))
                            1 -> _drawView.setStrokeWidth(resources.getDimension(R.dimen.draw_view_thickness_medium))
                            2 -> _drawView.setStrokeWidth(resources.getDimension(R.dimen.draw_view_thickness_large))
                        }
                        _selectedThickness = position
                        d3.dismiss()
                    }
                    dialog.setNegativeButton(R.string.cancel) { _, _ -> }
                    dialog.show()
                }
                R.id.btn_back -> (activity as CustomCategoryActivity).onBackPressed(TAG_INT)
                R.id.btn_done -> {
                    val tmpBitmap1 = _drawView.getBitmap()
                    val tmpBitmap2 = UtilDrawing.getResizedBitmap(
                            tmpBitmap1,
                            resources.getDimension(R.dimen.new_category_drawable_size).toInt(),
                            resources.getDimension(R.dimen.new_category_drawable_size).toInt()
                    )
                    val bitmap = UtilDrawing.getBitmapClippedCircle(tmpBitmap2)
                    _customCategoryViewModel.setImage(UtilDrawing.bitmapToBytes(bitmap))
                    (activity as CustomCategoryActivity).onNextPressed(TAG_INT)
                }
            }
        }
    }
}