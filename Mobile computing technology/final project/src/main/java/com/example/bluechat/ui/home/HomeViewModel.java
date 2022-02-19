package com.example.bluechat.ui.home;


import android.view.Menu;
import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bluechat.ListAdapter.UserAdapter;
import com.example.bluechat.User;

import java.util.List;

public class HomeViewModel extends ViewModel {


    //listview
    MutableLiveData<ListView> m_listview;


    public HomeViewModel() {
        m_listview = new MutableLiveData<>();


    }

    public LiveData<ListView> getListView() {
        return m_listview;
    }
}