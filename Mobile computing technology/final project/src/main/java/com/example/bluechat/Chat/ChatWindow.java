package com.example.bluechat.Chat;

import android.app.Activity;
import android.os.Bundle;

import com.example.bluechat.R;

public class ChatWindow extends Activity {
    String m_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window);
    }


}
