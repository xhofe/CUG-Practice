package com.example.bluechat.ui.dashboard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bluechat.Chat.OneChat;
import com.example.bluechat.ListAdapter.UserAdapter;
import com.example.bluechat.MainActivity;
import com.example.bluechat.R;
import com.example.bluechat.User;
import com.example.bluechat.Utils.BluetoothHelper;
import com.example.bluechat.Utils.Peer;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements Runnable{

    private DashboardViewModel dashboardViewModel;

    UserAdapter m_adapter;
    List<User> m_listdata;

    Bitmap m_sameIcon;


    @Override
    public void run() {
        while (true){
            try {
                m_handler.sendMessage(new android.os.Message());

                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Handler m_handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
//            List<Peer> peers = MainActivity.getClient().getHelper().getmPeers();
            List<Peer> peers = BluetoothHelper.getmOnlines();
            m_adapter.clear();

            for (int i = 0; i < peers.size(); i++)
            {
                User user = new User(1,peers.get(i).getDeviceName(),m_sameIcon);


                m_adapter.add(user);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        //同样的icon
        m_sameIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);

        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Bitmap me_icon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        m_listdata = new ArrayList<>();


        final ListView listView = root.findViewById(R.id.listView_dashBoard);
        m_adapter = new UserAdapter(this.getContext(), R.id.listView_dashBoard,m_listdata);
        listView.setAdapter(m_adapter);

        //给listAdapter添加事件
        listView.setOnItemClickListener(onClickListView);

        //启动线程扫描当前已连接设备显示在list view 中
        //接受消息
        new Thread(this).start();

        dashboardViewModel.getListView().observe(this, new Observer<ListView>() {
            @Override
            public void onChanged(@Nullable ListView v){
                System.out.println("list view changed");
            }
        });
        return root;
    }


    //todo: 加下来添加单对单聊天
    //添加item事件
    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            if(position == 0)//是群组
//            {
//                Intent intent = new Intent(getActivity(), GroupChat.class);
//                startActivity(intent);
//            }
            Intent intent = new Intent(getActivity(), OneChat.class);

            intent.putExtra("id",m_adapter.getItem(position).getId());
            intent.putExtra("name", m_adapter.getItem(position).getName());

            startActivity(intent);
        }
    };
}