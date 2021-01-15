//package com.kakeibo.ui.categories;
//
//import android.app.Application;
//
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//
//import com.kakeibo.SubApp;
//import com.kakeibo.data.CategoryDspStatus;
//import com.kakeibo.data.CategoryStatus;
//import com.kakeibo.data.DataRepository;
//
//import java.util.List;
//
//public class CategoryDspStatusViewModel extends AndroidViewModel {
//    private DataRepository _repository;
//
//    private LiveData<List<CategoryDspStatus>> _categoryDspStatuses;
//    private LiveData<List<CategoryStatus>> _categoryStatusesForDsp;
//
//    public CategoryDspStatusViewModel(Application application) {
//        super(application);
//        _repository = ((SubApp) application).getRepository();
//        _categoryDspStatuses = _repository.getCategoryDspStatuses();
//        _categoryStatusesForDsp = _repository.getCategoryStatusesForDsp();
//    }
//
//    public LiveData<List<CategoryDspStatus>> getAll() {
//        return _categoryDspStatuses;
//    }
//
//    public LiveData<List<CategoryStatus>> getDspCategoryStatuses() {
//        return _categoryStatusesForDsp;
//    }
//
//    public void updateAll(List<Integer> categoryCodes) {
////        mRepository.updateAllCategoryDspStatuses(categoryCodes);
//    }
//}
