package com.kakeibo.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kakeibo.MyExceptionHandler;
import com.kakeibo.R;
import com.kakeibo.util.UtilCategory;

import java.util.ArrayList;
import java.util.List;

public class CategoryReorderActivity extends AppCompatActivity
        implements SettingsCategoryEventListener {

    private static final String TAG = CategoryReorderActivity.class.getSimpleName();

    private CategoryPlacementReorderFragment _fragmentReorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_category_reorder);

        /*** this part is to handle unexpected crashes ***/
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
        if (getIntent().getBooleanExtra("crash", false)) {
            Log.e(TAG, "crashed");
        }

        /*** hide home button on actionbar ***/
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }

        _fragmentReorder = CategoryPlacementReorderFragment.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, _fragmentReorder)
                .commit();//frl_settings_category_reorder_container
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<Integer> list = new ArrayList<>(UtilCategory.getDspCategoryCodeList(getApplicationContext())); // ordered by location
        _fragmentReorder.setItemsOnGrid(list, new ArrayList<>());
    }

    @Override
    public void onNextPressed(int tag, List<Integer> list) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_mikan);
        dialog.setTitle("Determine how categories get displayed on INPUT screen");
        dialog.setMessage("Do you want to proceed with the specified categories with specified order");
        dialog.setPositiveButton(R.string.yes, (DialogInterface d, int which) -> {
            Toast.makeText(this, R.string.next, Toast.LENGTH_SHORT).show();
            UtilCategory.updateDspTable(getApplicationContext(), list);
            finish();
        });
        dialog.setNegativeButton(R.string.no, (DialogInterface d, int which) -> {});
        dialog.show();
    }

    @Override
    public void onBackPressed(int tag) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (_fragmentReorder!=null && _fragmentReorder._dgvCategory.isEditMode()) {
            _fragmentReorder._dgvCategory.stopEditMode();
        } else {
            super.onBackPressed();
        }
    }
}
