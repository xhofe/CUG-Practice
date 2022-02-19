package com.example.bluechat.ui.dashboard;

import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<ListView> m_listView;

    public DashboardViewModel() {
        m_listView = new MutableLiveData<>();
    }

    public LiveData<ListView> getListView() {
        return m_listView;
    }
}