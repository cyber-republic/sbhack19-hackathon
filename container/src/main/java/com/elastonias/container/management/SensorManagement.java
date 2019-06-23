package com.elastonias.container.management;

import com.elastonias.container.database.DatabaseInterface;
import com.elastonias.container.model.Product;
import com.elastonias.container.service.elastos.ElastosCarrier;
import com.elastonias.container.service.elastos.blockchain.DIDManagement;
import com.google.gson.JsonObject;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SensorManagement{
    private ElastosCarrier elastosCarrier;
    private DIDManagement didManagement;
    private ExecutorService executorService;
    private Future<?> future;
    private String controllerUserId;
    private boolean isRunning=false;

    public SensorManagement(ElastosCarrier elastosCarrier, DIDManagement didManagement){
        this.elastosCarrier=elastosCarrier;
        this.didManagement=didManagement;
    }

    public void setControllerUserId(String controllerUserId){
        this.controllerUserId=controllerUserId;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public void start(){
        isRunning=true;
        executorService=Executors.newSingleThreadExecutor();
        Runnable runnable=new Runnable(){
            public void run(){
                while(true){
                    try{
                        int value=getRandomNumber(15,32);
                        JsonObject jsonObject=new JsonObject();
                        jsonObject.addProperty("command", "addDataRecord");
                        jsonObject.addProperty("timestamp", new Date().getTime());
                        jsonObject.addProperty("value", value);
                        elastosCarrier.sendFriendMessage(controllerUserId, jsonObject.toString());
                        eventProcess(value);
                        TimeUnit.SECONDS.sleep(3);
                    }
                    catch(InterruptedException e){
                        isRunning=false;
                        break;
                    }
                }
            }
        };
        future=executorService.submit(runnable);
    }

    public void stop(){
        future.cancel(true);
        executorService.shutdown();
        isRunning=false;
    }

    public void eventProcess(int value){
        if(value<16 || value>30){
            long timestamp=new Date().getTime();
            String strTimestamp=String.valueOf(timestamp);
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("command", "addEvent");
            jsonObject.addProperty("timestamp", timestamp);
            jsonObject.addProperty("condition", "value<16 || value>30");
            jsonObject.addProperty("value", value);
            jsonObject.addProperty("notification", "product is damaged");
            elastosCarrier.sendFriendMessage(controllerUserId, jsonObject.toString());
            didManagement.saveData(strTimestamp, jsonObject);
        }
    }

    public static int getRandomNumber(int min, int max){
        Random random=new Random();
        return random.nextInt((max-min)+1)+min;
    }
}
