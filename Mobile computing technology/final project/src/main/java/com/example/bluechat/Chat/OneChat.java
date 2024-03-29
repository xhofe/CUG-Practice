package com.example.bluechat.Chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bluechat.MainActivity;
import com.example.bluechat.R;
import com.example.bluechat.User;
import com.example.bluechat.UserConfig.Me;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.MessageView;

import java.util.ArrayList;
import java.util.List;

public class OneChat extends Activity implements View.OnClickListener,Runnable {
    private Button m_send_button;
    private TextView m_input_text;
    private MessageView m_message_view;
    private List<Message> m_messages;
    private User m_me;
    private User m_you;

    Bitmap m_sameIcon;

    Handler m_handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            List<com.example.bluechat.Utils.Message> Messages = MainActivity.getClient().getPrivateMsgs(m_you.getName());
            m_message_view.removeAll();

            for (int i = 0; i < Messages.size(); i++)
            {
                User user = new User(1,Messages.get(i).getFrom(),m_sameIcon);

                boolean isMe = true;
                if(Messages.get(i).getFrom() == m_me.getName())
                    isMe = true;
                else
                    isMe = false;

                Message message = new Message.Builder().setUser(user).setText(Messages.get(i).getText())
                        .setRight(isMe).build();
                m_message_view.setMessage(message);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.send_button_one_chat:
                String message = m_input_text.getText().toString();
                if(!message.isEmpty())
                    this.send_message(message);
                break;
        }
    }


    public void send_message(String message){
        m_input_text.setText("");

        Message temp =new Message.Builder()
                .setUser(m_me)
                .setText(message)
                .setRight(true)
                .build();
        m_message_view.setMessage(temp);

        com.example.bluechat.Utils.Message x_message = com.example.bluechat.Utils.Message.generatePrivate(
                m_me.getName(),m_you.getName(),message,m_me.getName());

        MainActivity.getClient().sendMsg(x_message);
    }

    void init(){
        m_sameIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);

        Bitmap me_icon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        Bitmap you_icon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);

        m_send_button = findViewById(R.id.send_button_one_chat);
        m_input_text = findViewById(R.id.input_text_one_chat);
        m_message_view = findViewById(R.id.message_view_one_chat);
        m_me = new User(Integer.parseInt(Me.m_me.getId()), Me.m_me.getName(), Me.m_me.getIcon());

        //init you
        Intent intent = this.getIntent();

        m_you = new User(Integer.parseInt(intent.getStringExtra("id")),intent.getStringExtra("name"),you_icon);

        m_messages = new ArrayList<>();
        m_send_button.setOnClickListener(this);
        m_message_view.init(m_messages);

        //接受消息
        new Thread(this).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_chat);

        init();
    }


    //循环接受消息
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
}
