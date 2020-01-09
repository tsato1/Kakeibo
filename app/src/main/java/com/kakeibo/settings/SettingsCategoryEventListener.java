package com.kakeibo.settings;

import java.util.List;

public interface SettingsCategoryEventListener {
    void onNextPressed(int tag, List<Integer> list);
    void onBackPressed(int tag);
}