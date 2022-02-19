package com.example.bluechat.Utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.bluechat.MainActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * created by 徐鸿飞 on 2019-12-26
 */
public class Peer extends Thread{
    private BluetoothSocket mSocket=null;
    private BluetoothDevice device;
    private String name;
    private final String key="bluechat";

    public Peer(String _name){
        name=_name;
    }

    public Peer(BluetoothSocket _socket){
        mSocket=_socket;
        device=mSocket.getRemoteDevice();
        name=device.getName();
        this.start();
    }

    /**
     * 获取设备名
     * @return 设备名
     */
    public String getDeviceName() {
        return name;
    }

    /**
     * 单设备发送消息
     * @param msg 消息
     */
    public void send(final String msg){
        new Thread(){
            @Override
            public void run() {
                try {
                    DataOutputStream os = new DataOutputStream(mSocket.getOutputStream());
                    os.writeUTF(SymmetricEncoder.AESEncode(key,msg));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 接收消息
     */
    @Override
    public void run() {
        while (true){
            try {
                DataInputStream is = new DataInputStream(mSocket.getInputStream());
                StringBuilder _msg=new StringBuilder();
//                byte[] bytes=new byte[1024];
//                int len=is.read(bytes);
//                for (int i = 0; i < len; i++) {
//                    _msg.append((char)bytes[i]);
//                }
                //String msg=_msg.toString();
                String msg = is.readUTF();
                //TODO
                MainActivity.getClient().handleMsg(Message.parseMsg(AESEncode.AESDncode(key,msg)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
