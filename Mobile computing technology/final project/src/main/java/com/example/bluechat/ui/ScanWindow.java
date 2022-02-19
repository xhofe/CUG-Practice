package com.example.bluechat.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bluechat.Chat.GroupChat;
import com.example.bluechat.ListAdapter.DeviceAdapter;
import com.example.bluechat.ListAdapter.UserAdapter;
import com.example.bluechat.MainActivity;
import com.example.bluechat.R;

import java.util.ArrayList;
import java.util.List;

public class ScanWindow extends Activity {


    DeviceAdapter m_adapter;
    List<BluetoothDevice> m_deviceData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_window);




        m_deviceData = MainActivity.getClient().getHelper().getMdevices();

        final ListView listView = findViewById(R.id.listView_scan);
        m_adapter = new DeviceAdapter(this.getApplicationContext(),R.id.listView_scan,m_deviceData);
        listView.setAdapter(m_adapter);

        //给listAdapter添加事件
        listView.setOnItemClickListener(onClickListView);


        // 注册这个 BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver,filter);

        startScan();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    //添加item事件
    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = m_adapter.getItem(position);
            MainActivity.getClient().getHelper().connect(device);
            
//            if(position == 0)//是群组
//            {
//                Intent intent = new Intent(ScanWindow.this, GroupChat.class);
//                startActivity(intent);
//            }
        }
    };


    public void startScan(){
        MainActivity.getClient().getHelper().scanDevice();
    }


    // 创建一个接受 ACTION_FOUND 的 BroadcastReceiver
    final BroadcastReceiver mReceiver = new BroadcastReceiver(){

        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            // 当 Discovery 发现了一个设备
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                // 从 Intent 中获取发现的 BluetoothDevice
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 将名字和地址放入要显示的适配器中
//                    mArrayAdapter.add(device.getName + "\n" + device.getAddress());
//                    mDeviceMap.put(device.getName(),device.getAddress());、
//                if (!MainActivity.getClient().getHelper().getMdevices().contains(device)){
                    MainActivity.getClient().getHelper().getMdevices().add(device);

                    System.out.println("add: "+device.getAddress());
                    m_adapter.add(device);
//                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(context,"scan finished",Toast.LENGTH_SHORT).show();
            }
        }
    };


}
