package com.kakeibo.ui.categories;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kakeibo.SubApp;
import com.kakeibo.data.DataRepository;
import com.kakeibo.data.CategoryStatus;

import java.util.List;

public class CategoryStatusViewModel extends AndroidViewModel {
    private DataRepository _repository;

    private LiveData<List<CategoryStatus>> _categories;
    private LiveData<List<Integer>> _categoryCodes;

    public CategoryStatusViewModel(Application application) {
        super(application);
        _repository = ((SubApp) application).getRepository();
//        _categories = _repository.getCategories();
        _categoryCodes = _repository.getCategoryCodes();
    }

    public LiveData<List<CategoryStatus>> getCategoryStatuses() { return _categories; }

    public LiveData<List<Integer>> getCategoryCodes() { return _categoryCodes; }

    public void insert(CategoryStatus categoryStatus) { _repository.insertCategoryStatus(categoryStatus); }
}
