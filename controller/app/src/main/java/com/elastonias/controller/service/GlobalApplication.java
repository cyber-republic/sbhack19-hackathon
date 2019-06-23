package com.elastonias.controller.service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.elastonias.controller.service.elastos.ElastosCarrier;


public class GlobalApplication extends Application{
    public static final String CHANNEL_ID="ControllerChannel";
    private static ElastosCarrier elastosCarrier;
    private static boolean stayConnected=false;
    private static boolean appActive=true;

    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel=new NotificationChannel(CHANNEL_ID, "Controller Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static ElastosCarrier getElastosCarrier(){
        return elastosCarrier;
    }

    public static void setElastosCarrier(ElastosCarrier newElastosCarrier){
        elastosCarrier=newElastosCarrier;
    }

    public static LocalRepository getLocalRepository(){
        return elastosCarrier.getLocalRepository();
    }

    public boolean isCarrierConnected(){
        return elastosCarrier.isConnected();
    }

    public static void setStayConnected(boolean newStayConnected){
        stayConnected=newStayConnected;
    }

    public static boolean getStayConnected(){
        return stayConnected;
    }

    public static void setAppActive(boolean newAppActive){
        appActive=newAppActive;
    }

    public static boolean getAppActive(){
        return appActive;
    }
}
