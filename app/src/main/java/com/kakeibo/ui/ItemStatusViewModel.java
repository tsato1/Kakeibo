//package com.kakeibo.ui;
//
//import android.app.Application;
//
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//
//import com.kakeibo.SubApp;
//import com.kakeibo.data.DataRepository;
//import com.kakeibo.data.ItemStatus;
//
//import java.util.List;
//
//public class ItemStatusViewModel extends AndroidViewModel {
//
//    private DataRepository _repository;
//
//    private LiveData<List<ItemStatus>> _items;
//
//    public ItemStatusViewModel(Application application) {
//        super(application);
//        SubApp subApp = ((SubApp) application);
//        _repository = subApp.getRepository();
//        _items = _repository.getAll();
//    }
//
//    LiveData<List<ItemStatus>> getAll() { return _items; }
//
//    public void insert(ItemStatus itemStatus) { _repository.insertItemStatus(itemStatus); }
//}
