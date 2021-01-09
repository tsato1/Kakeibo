package com.kakeibo.ui.categories;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kakeibo.R;
import com.kakeibo.data.CategoryStatus;
import com.kakeibo.databinding.ItemGridBinding;
import com.kakeibo.ui.ItemSaveListener;

import java.util.ArrayList;
import java.util.List;

/*
 * Used in TabFragment1, Settings
 */
public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.ViewHolder> {
    private static final String TAG = CategoryGridAdapter.class.getSimpleName();

    private final ItemSaveListener _itemSaveListener;

    private List<CategoryStatus> _categoryStatusList = new ArrayList<>();

    public CategoryGridAdapter(ItemSaveListener itemSaveListener) {
        this._itemSaveListener = itemSaveListener;
    }

    public void setCategoryStatuses(List<CategoryStatus> categoryStatusList) {
        this._categoryStatusList = categoryStatusList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGridBinding itemGridBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_grid, parent, false);
        return new ViewHolder(itemGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryStatus categoryStatus = _categoryStatusList.get(position);
        holder.bind(categoryStatus, _itemSaveListener);
    }

    @Override
    public int getItemCount() {
        return _categoryStatusList != null? _categoryStatusList.size(): 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final private ItemGridBinding itemGridBinding;

         ViewHolder (ItemGridBinding itemGridBinding) {
            super(itemGridBinding.getRoot());
            this.itemGridBinding = itemGridBinding;
        }

        public void bind(CategoryStatus categoryStatus, ItemSaveListener itemSaveListener) {
            itemGridBinding.setCategory(categoryStatus);
            itemGridBinding.setItemSaveListener(itemSaveListener);
            itemGridBinding.executePendingBindings();
        }
    }
}
