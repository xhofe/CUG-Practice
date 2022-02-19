package com.example.bluechat.Utils;

import androidx.annotation.NonNull;

/**
 * 消息组成
 * 约定:
 * 连接消息:Connect##from##middle
 * 公开消息:Public##from##content##middle
 * 私密消息:Private##from##to##content##middle
 * created by 徐鸿飞 on 2019-12-26
 */
public class Message {
//    private static final String Public = "0000000000000000";
    private String  msgtypes;
    private String from;
    private String to;

    private String text;
    private String middle;//中间节点
    public static final String CONNECT="01";
    public static final String PUBLIC="02";
    public static final String PRIVATE="03";

    /**
     * 解析字符串为消息
     * @param msg 接受的消息（字符串）
     * @return 解析之后的消息
     */
    public static Message parseMsg(String msg){
        return new Message(msg);
    }

    /**
     * 构造连接消息
     * @param from 来源
     * @param middle 中间人
     * @return 消息
     */
    public static Message generateConnect(String from,String middle){
        return new Message(CONNECT,from,"","",middle);
    }

    /**
     * 构造公共消息
     * @param from 来源
     * @param content 内容
     * @param middle 中间人
     * @return 消息
     */
    public static Message generatePublic(String from,String content,String middle){
        return new Message(PUBLIC,from,"",content,middle);
    }

    /**
     * 构造私密消息
     * @param from 来源
     * @param to 接收者
     * @param content 内容
     * @param middle 中间人
     * @return 消息
     */
    public static Message generatePrivate(String from,String to,String content,String middle){
        return new Message(PRIVATE,from,to,content,middle);
    }

    /**
     * Constructor
     */
    private Message(String msg){
        String[] strings=msg.split("##");
        msgtypes=strings[0];
        switch (msgtypes){
            case CONNECT:
            {
                from=strings[1];
                middle=strings[2];
                break;
            }
            case PUBLIC:
            {
                from=strings[1];
                text=strings[2];
                middle=strings[3];
                break;
            }
            case PRIVATE:
            {
                from=strings[1];
                to=strings[2];
                text=strings[3];
                middle=strings[4];
                break;
            }
        }
    }

    private Message(String msgtypes, String from, String to,String text,String middle) {
        this.msgtypes = msgtypes;
        this.from = from;
        this.to = to;
        this.text=text;
        this.middle=middle;
    }

    /**
     * getter and setter
     * @return
     */
    public String  getMsgtypes() {
        return msgtypes;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public void setMsgtypes(String  msgtypes) {
        this.msgtypes = msgtypes;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setText(String text) {
        this.text = text;
    }


    @NonNull
    @Override
    public String toString() {
        switch (msgtypes){
            case CONNECT:
            {
                return msgtypes+"##"+from+"##"+middle;
            }
            case PUBLIC:
            {
                return msgtypes+"##"+from+"##"+text+"##"+middle;
            }
            case PRIVATE:
            {
                return msgtypes+"##"+from+"##"+to+"##"+text+"##"+middle;
            }
        }
        return "";
    }
}
