package com.example.bluechat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.bluechat.Chat.ChatWindow;
import com.example.bluechat.UserConfig.Me;
import com.example.bluechat.Utils.Client;
import com.example.bluechat.ui.ScanWindow;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static Client m_client;
    static List<String> m_onlines;

    public static Client getClient(){
        return m_client;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_cart://监听菜单按钮
                System.out.println("start scan");
                Intent intent = new Intent(MainActivity.this, ScanWindow.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListner();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        m_client = new Client(this);
        m_onlines = new ArrayList<>();


        //初始化Me
        Bitmap me_icon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        Me.m_me = new User(1,MainActivity.getClient().getHelper().getNAME(),me_icon);
    }


    public void initListner(){

    }

    @Override
    public void onClick(View view){
//        switch (view.getId())
//        {
//            case R.id.button:
//                Intent intent = new Intent(MainActivity.this, ChatWindow.class);
//                startActivity(intent);
//                break;
//            default:
//                break;
//        }
    }

}
