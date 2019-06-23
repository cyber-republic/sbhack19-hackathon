package com.elastonias.controller;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.elastonias.controller.service.ElastosService;

public class MainActivity extends AppCompatActivity{
    public static final int ACTION_REQUEST_ADD_CONTAINER=0;
    public static final int PERMISSION_REQUEST_CODE_CAMERA=0;

    private AppCompatButton addReceiveButton;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("MainActivity - onCreate");

        addReceiveButton=findViewById(R.id.addReceiveButton);
        addReceiveButton.setOnClickListener(buttonView -> {
            Intent intent=new Intent(MainActivity.this, AddContainerActivity.class);
            startActivityForResult(intent, ACTION_REQUEST_ADD_CONTAINER);
        });

        initMainNavigation();

        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ACTION_REQUEST_ADD_CONTAINER && resultCode==RESULT_OK){
            Snackbar.make(addReceiveButton, "Request to container has been sent.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        System.out.println("MainActivity - onStart");

        Intent serviceIntent=new Intent(this, ElastosService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void initMainNavigation(){
        viewPager=findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()){
            @Override
            public Fragment getItem(int i){
                Fragment fragment=null;
                switch(i){
                    case 0:
                        fragment=new MainFragment();
                        break;
                }
                return fragment;
            }

            @Override
            public int getCount(){
                return 1;
            }
        });
    }
}