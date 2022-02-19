package com.example.bluechat.Utils;

import android.media.MediaSession2;

import com.example.bluechat.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * created by 徐鸿飞 on 2019-12-26
 */
public class Client {
    private BluetoothHelper helper=null;
    private MainActivity activity=null;

    private List<Message> privateMsgs=new ArrayList<>();
    private List<Message> publicMsgs=new ArrayList<>();

    //    private String m_name;
    public Client(MainActivity activity){
        this.activity=activity;
        helper=new BluetoothHelper(activity);
        helper.openBluetooth();
        helper.oldDevice();
        helper.visibility();
        helper.accept();
    }

    public BluetoothHelper getHelper() {
        return helper;
    }

    /**
     * 广播消息
     * @param msg 消息
     */
    public void broadcast(Message msg){
        List<Peer> peers=helper.getmPeers();
        for (Peer peer :
                peers) {
            peer.send(msg.toString());
        }
    }

    /**
     * 广播消息并排除不要广播的设备
     * @param msg 消息
     * @param no 联系人
     */
    public void broadcast(Message msg,String no){
        List<Peer> peers=helper.getmPeers();
        for (Peer peer :
                peers) {
            if (peer.getDeviceName().equals(no)){
                continue;
            }
            peer.send(msg.toString());
        }
    }

    /**
     * 发送消息并将消息加入到消息列表
     * @param message 要发送到消息
     */
    public void sendMsg(Message message){
        broadcast(message);
        if (message.getMsgtypes().equals(Message.PUBLIC)){
            publicMsgs.add(message);
        }
        if (message.getMsgtypes().equals(Message.PRIVATE)){
            privateMsgs.add(message);
        }
    }

    /**
     * 处理接受的消息
     * @param msg 消息
     */
    public void handleMsg(Message msg){
        switch (msg.getMsgtypes()){
            case Message.CONNECT:{
                boolean isAdd = BluetoothHelper.addOnline(new Peer(msg.getFrom()));
                if(isAdd)
                    break;
                String middle=msg.getMiddle();
                msg.setMiddle(helper.getNAME());
                broadcast(msg,middle);
                break;
            }
            case Message.PUBLIC:{
                publicMsgs.add(msg);
                String middle=msg.getMiddle();
                msg.setMiddle(helper.getNAME());
                broadcast(msg,middle);
                break;
            }
            case Message.PRIVATE:{
                if (msg.getTo().equals(helper.getNAME())){
                    //发送给自己的消息
                    privateMsgs.add(msg);
                }else {
                    //不是自己的继续广播
                    String middle=msg.getMiddle();
                    msg.setMiddle(helper.getNAME());
                    broadcast(msg,middle);
                }
                break;
            }
        }
    }


    public List<Message> getPublicMsgs() {
        return publicMsgs;
    }

    /**
     * 返回指定联系人的消息列表
     * @param name 指定联系人名称
     * @return 消息列表
     */
    public List<Message> getPrivateMsgs(String name){
        List<Message> messages=new ArrayList<>();
        for (Message message:privateMsgs){
            if (message.getFrom().equals(name)||message.getTo().equals(name)){
                messages.add(message);
            }
        }
        return messages;
    }
}
