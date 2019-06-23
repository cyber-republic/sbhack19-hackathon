package com.elastonias.controller.service.elastos;

import android.content.Context;

import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.database.entity.DataRecord;
import com.elastonias.controller.database.entity.Event;
import com.elastonias.controller.database.entity.Product;
import com.elastonias.controller.service.LocalRepository;
import com.elastonias.controller.service.elastos.common.Synchronizer;
import com.elastonias.controller.service.elastos.common.TestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.CarrierHandler;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.CarrierException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ElastosCarrier extends Thread{
    private final static String CONTROLLER_CONNECTION_KEYWORD="connection_key";
    private static Synchronizer syncher=new Synchronizer();
    private static TestOptions options;
    private static CarrierHandler carrierHandler;
    private static Carrier carrier;
    private boolean isConnected=false;
    private static LocalRepository localRepository;

    public ElastosCarrier(Context context){
        localRepository=new LocalRepository(context);
        options=new TestOptions(getAppPath(context));
        carrierHandler=new CarrierHandler();
    }

    public LocalRepository getLocalRepository(){
        return localRepository;
    }

    public void run(){
        try{
            Carrier.initializeInstance(options, carrierHandler);
            carrier=Carrier.getInstance();
            carrier.start(0);

            synchronized(carrier){
                carrier.wait();
            }
        }
        catch(CarrierException| InterruptedException e){}
    }

    public void kill(){
        carrier.kill();
    }

    public boolean isConnected(){
        return isConnected;
    }

    public String getAddress(){
        String address="undefined";
        try{
            address=carrier.getAddress();
        }
        catch(CarrierException ce){
            address="undefined";
        }
        syncher.wakeup();
        return address;
    }

    public String getUserId(){
        String userId="undefined";
        try{
            userId=carrier.getUserId();
        }
        catch(CarrierException ce){
            userId="undefined";
        }
        syncher.wakeup();
        return userId;
    }

    public boolean isValidAddress(String address){
        return Carrier.isValidAddress(address);
    }

    public boolean isValidUserId(String userId){
        return Carrier.isValidId(userId);
    }

    public boolean addFriend(Container container){
        boolean response=true;
        try{
            carrier.addFriend(container.getAddress(), CONTROLLER_CONNECTION_KEYWORD);
            localRepository.setTempContainer(container);
        }
        catch(CarrierException ce){
            response=false;
        }
        syncher.wakeup();
        return response;
    }

    public boolean removeFriend(String userId){
        boolean response=true;
        try{
            carrier.removeFriend(userId);
        }
        catch(CarrierException ce){
            response=false;
        }
        syncher.wakeup();
        return response;
    }

    public boolean sendFriendMessage(String userId, String message){
        boolean response=true;
        try{
            carrier.sendFriendMessage(userId, message);
        }
        catch(CarrierException ce){
            response=false;
        }
        syncher.wakeup();
        return response;
    }

    private class CarrierHandler extends AbstractCarrierHandler{
        @Override
        public void onReady(Carrier carrier){
            synchronized(carrier){
                carrier.notify();
            }
        }

        @Override
        public void onConnection(Carrier carrier, ConnectionStatus status){
            System.out.println("CarrierHandler - onConnection - "+status);
            System.out.println("address: "+getAddress());
            System.out.println("userId: "+getUserId());
            //removeFriend("9KYKZMbmRwS9mg8nFcVVv5XyJvt3W53rvo58VB5BQTLt");
            syncher.wakeup();
        }

        @Override
        public void onFriends(Carrier carrier, List<FriendInfo> friends){
            System.out.println("CarrierHandler - onFriends - "+friends);
            syncher.wakeup();
        }

        @Override
        public void onFriendConnection(Carrier carrier, String friendId, ConnectionStatus status){
            System.out.println("CarrierHandler - onFriendConnection - "+friendId+" - "+status);
            Container container=localRepository.getContainerByUserId(friendId);
            if(container!=null){
                if(status==ConnectionStatus.Connected){
                    container.setConnectionState("online");
                    if(container.getState().equals("pending")){
                        container.setState("active");
                        JsonObject jsonObject=new JsonObject();
                        jsonObject.addProperty("command", "getProducts");
                        sendFriendMessage(friendId, jsonObject.toString());
                    }
                }
                else if(status==ConnectionStatus.Disconnected){
                    container.setConnectionState("offline");
                }
                localRepository.updateContainer(container);
            }
            syncher.wakeup();
        }

        @Override
        public void onFriendAdded(Carrier carrier, FriendInfo info){
            System.out.println("CarrierHandler - onFriendAdded - "+info);
            Container container=localRepository.getContainerByUserId(info.getUserId());
            if(container==null){
                Container newContainer=localRepository.getTempContainer();
                newContainer.setUserId(info.getUserId());
                localRepository.insertContainer(newContainer);
            }
            syncher.wakeup();
        }

        @Override
        public void onFriendRemoved(Carrier carrier, String friendId){
            System.out.println("CarrierHandler - onFriendRemoved - "+friendId);
            syncher.wakeup();
        }

        @Override
        public void onFriendMessage(Carrier carrier, String from, byte[] message){
            String messageText=new String(message);
            System.out.println("CarrierHandler - onFriendMessage - "+from+" - "+messageText);
            Gson gson=new Gson();
            JsonObject resultObject=gson.fromJson(messageText, JsonObject.class);
            String command=resultObject.get("command").getAsString();
            if(command.equals("containerReceived")){
                Container container=localRepository.getContainerByUserId(from);
                container.setProcessState("delivered");
                container.setAddress("undefined");
                container.setUserId("undefined");
                localRepository.updateContainer(container);
                removeFriend(from);
            }
            else if(command.equals("addProduct")){
                JsonObject productObject=resultObject.getAsJsonObject("product");
                String did=productObject.get("did").getAsString();
                String privateKey=productObject.get("privateKey").getAsString();
                String publicKey=productObject.get("publicKey").getAsString();

                Container container=localRepository.getContainerByUserId(from);
                Product product=localRepository.getProductByDid(did);
                if(product==null){
                    product=new Product(0, did, privateKey, publicKey, container.getId(), true, true);
                    localRepository.insertProduct(product);
                }
            }
            else if(command.equals("addDataRecord")){
                long timestamp=resultObject.get("timestamp").getAsLong();
                int value=resultObject.get("value").getAsInt();
                DataRecord dataRecord=localRepository.getDataRecordByControllerUserId(from);
                if(dataRecord==null){
                    dataRecord=new DataRecord(0, timestamp, value, from);
                    localRepository.insertDataRecord(dataRecord);
                }
                else{
                    dataRecord.setTimestamp(timestamp);
                    dataRecord.setValue(value);
                    localRepository.updateDataRecord(dataRecord);
                }
            }
            else if(command.equals("addEvent")){
                long timestamp=resultObject.get("timestamp").getAsLong();
                String condition=resultObject.get("condition").getAsString();
                int value=resultObject.get("value").getAsInt();
                String notification=resultObject.get("notification").getAsString();
                Event event=new Event(0, timestamp, condition, value, notification, from);
                localRepository.insertEvent(event);
            }
            syncher.wakeup();
        }
    }

    private String getAppPath(Context context){
        File file=context.getFilesDir();
        return file.getAbsolutePath();
    }
}
