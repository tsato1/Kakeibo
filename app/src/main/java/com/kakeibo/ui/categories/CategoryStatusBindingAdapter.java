package com.kakeibo.ui.categories;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.kakeibo.data.CategoryStatus;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilDrawing;

public class CategoryStatusBindingAdapter {
    @BindingAdapter("setImage")
    public static void setImage(ImageView imageView, CategoryStatus categoryStatus) {
        if (categoryStatus.getCode() < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
            imageView.setImageResource(categoryStatus.getDrawable());
        } else {
            imageView.setImageBitmap(UtilDrawing.bytesToBitmap(categoryStatus.getImage()));
        }
    }
}
