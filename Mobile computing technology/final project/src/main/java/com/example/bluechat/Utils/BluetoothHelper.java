package com.example.bluechat.Utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.example.bluechat.MainActivity;
import com.example.bluechat.UserConfig.Me;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * created by 徐鸿飞 on 2019-12-26
 */
public class BluetoothHelper {
    private MainActivity mMainActivity=null;
    private BluetoothAdapter mBluetoothAdapter=null;
    private static final int REQUEST_ENBLE_BT = 2;
//    private Map<String ,String > mDeviceMap =null;
    private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private String NAME;
    private BluetoothServerSocket mServerSocket;

//    private List<BluetoothSocket> msockets=new ArrayList<>();
    private List<BluetoothDevice> mdevices=new ArrayList<>();
    private List<Peer> mPeers=new ArrayList<>();

    private static List<Peer> mOnlines=new ArrayList<>();

    static public boolean addOnline(Peer peer){
        if (!containsOnline(peer)){
            mOnlines.add(peer);
            return true;
        }
        return  false;
    }

    public static List<Peer> getmOnlines() {
        return mOnlines;
    }

    static public boolean containsOnline(Peer peer){
        for (Peer p :
                mOnlines) {
            if (p.getDeviceName() == peer.getDeviceName()){
                return true;
            }
        }
        return false;
    }

    /**
     * 传入MainActivity构造类
     * @param mainActivity
     */
    public BluetoothHelper(MainActivity mainActivity){
        mMainActivity=mainActivity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            // 说明此设备不支持蓝牙操作
            Toast.makeText(mMainActivity.getApplicationContext(), "not support bluetooth,exit soon", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
//        mDeviceMap =new HashMap<>();
//        NAME=mBluetoothAdapter.getName();
    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth(){
        // 没有开始蓝牙
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mMainActivity.startActivityForResult(enableBtIntent,REQUEST_ENBLE_BT);
        }
    }

    /**
     * 加入旧设备
     */
    public void oldDevice(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0){
            for(BluetoothDevice device:pairedDevices){
                // 把名字和地址取出来添加到适配器中
//                mArrayAdapter.add(device.getName()+"\n"+ device.getAddress());
//                mDeviceMap.put(device.getName(),device.getAddress());
                mdevices.add(device);
            }
        }
    }

    /**
     * 扫描设备
     */
    public void scanDevice(){
        boolean res=mBluetoothAdapter.startDiscovery();
        System.out.println(res);
    }


    /**
     * 打开蓝牙可见性
     */
    public void visibility(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        mMainActivity.startActivityForResult(discoverableIntent,0);
    }

    /**
     * 新起一个线程接受连接
     */
    public void accept(){
        class AcceptThread extends Thread{
            public AcceptThread()throws Exception{
                mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mBluetoothAdapter.getName(),MY_UUID);
            }

            public void run(){
                try {
                    BluetoothSocket socket = null;
                    while(true){
                        socket = mServerSocket.accept();
                        if(socket!=null){
                            // 自定义方法
//                            manageConnectedSocket(socket);
                            newconnect(socket);
//                            mServerSocket.close();
//                            break;
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            public void cancle(){
                try {
                    mServerSocket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        try{
            AcceptThread acceptThread=new AcceptThread();
            acceptThread.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 新起一个线程发起连接
     */
    public void connect(BluetoothDevice device){
        try{
            if (device.getBondState() == BluetoothDevice.BOND_NONE){
                ClsUtils.createBond(device.getClass(), device);}
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        class ConnectThread extends Thread{
            private BluetoothDevice m_Device;
            private BluetoothSocket m_Socket;
            public ConnectThread(BluetoothDevice device)throws Exception{
                m_Device = device;
                // 这里的 UUID 需要和服务器的一致
                m_Socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }

            public void run(){
                // 关闭发现设备
                mBluetoothAdapter.cancelDiscovery();
                try{
                    m_Socket.connect();
                    newconnect(m_Socket);
                }catch(IOException connectException){
                    try{
                        m_Socket.close();
                        connectException.printStackTrace();
                    }catch(IOException closeException){
                        return;
                    }
                }
                // 自定义方法
//                manageConnectedSocket(mmSocket);


            }

            public void cancle(){
                try{
                    m_Socket.close();
                }catch(IOException closeException){
                    closeException.printStackTrace();
                }
            }
        }
        try {
            ConnectThread connectThread=new ConnectThread(device);
            connectThread.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 新增了一个连接
     * @param socket
     */
    public void newconnect(BluetoothSocket socket){
        //msockets.add(socket);
        //TODO
        Peer peer=new Peer(socket);
        mPeers.add(peer);
        Message connect= Message.generateConnect(getNAME(),getNAME());
        MainActivity.getClient().sendMsg(connect);
//        mOnlines.add(peer);
    }


    /**
     * setter and getter
     * @return
     */
    public MainActivity getmMainActivity() {
        return mMainActivity;
    }

    public void setmMainActivity(MainActivity mMainActivity) {
        this.mMainActivity = mMainActivity;
    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public static int getRequestEnbleBt() {
        return REQUEST_ENBLE_BT;
    }

    public static UUID getMyUuid() {
        return MY_UUID;
    }

    public String getNAME() {
        return mBluetoothAdapter.getName();
    }

    public void setNAME(String NAME) {
        mBluetoothAdapter.setName(NAME);
    }

    public BluetoothServerSocket getmServerSocket() {
        return mServerSocket;
    }

    public void setmServerSocket(BluetoothServerSocket mServerSocket) {
        this.mServerSocket = mServerSocket;
    }

    public List<BluetoothDevice> getMdevices() {
        return mdevices;
    }

    public void setMdevices(List<BluetoothDevice> mdevices) {
        this.mdevices = mdevices;
    }

    public List<Peer> getmPeers() {
        return mPeers;
    }

    public void setmPeers(List<Peer> mPeers) {
        this.mPeers = mPeers;
    }
}
