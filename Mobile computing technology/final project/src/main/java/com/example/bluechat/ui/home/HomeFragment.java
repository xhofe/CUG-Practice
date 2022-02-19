package com.example.bluechat.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bluechat.Chat.GroupChat;
import com.example.bluechat.ListAdapter.UserAdapter;
import com.example.bluechat.MainActivity;
import com.example.bluechat.R;
import com.example.bluechat.User;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    UserAdapter m_adapter;
    List<User> m_listdata;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Bitmap me_icon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        m_listdata = new ArrayList<>();
        m_listdata.add(new User(1,"group",me_icon));
        m_listdata.add(new User(33,"fuck",me_icon));

        final ListView listView = root.findViewById(R.id.listView_home);
        m_adapter = new UserAdapter(this.getContext(),R.id.listView_home,m_listdata);
        listView.setAdapter(m_adapter);

        //给listAdapter添加事件
        listView.setOnItemClickListener(onClickListView);


        homeViewModel.getListView().observe(this, new Observer<ListView>() {
            @Override
            public void onChanged(@Nullable ListView v){
                System.out.println("list view changed");
            }
        });
        return root;
    }


    //添加item事件
    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0)//是群组
            {
                Intent intent = new Intent(getActivity(), GroupChat.class);
                startActivity(intent);
            }
        }
    };

}