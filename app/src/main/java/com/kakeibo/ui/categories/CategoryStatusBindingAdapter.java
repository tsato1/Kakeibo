package com.kakeibo.ui.categories;

import android.content.Context;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.kakeibo.data.CategoryStatus;
import com.kakeibo.util.UtilCategory;
import com.kakeibo.util.UtilDrawing;

public class CategoryStatusBindingAdapter {
    @BindingAdapter({"context", "cateogry"})
    public static void setImage(ImageView imageView, Context context, CategoryStatus categoryStatus) {
        if (categoryStatus.getCode() < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
            imageView.setImageResource(
                    UtilDrawing.getDrawableIdFromIconName(context, categoryStatus.getDrawable()));
        } else {
            imageView.setImageBitmap(UtilDrawing.bytesToBitmap(categoryStatus.getImage()));
        }
    }
}
