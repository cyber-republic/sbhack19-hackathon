package com.elastonias.container.service.elastos;

import com.elastonias.container.App;
import com.elastonias.container.management.SensorManagement;
import com.elastonias.container.model.Controller;
import com.elastonias.container.model.DIDObject;
import com.elastonias.container.model.Product;
import com.elastonias.container.service.elastos.common.Synchronizer;
import com.elastonias.container.service.elastos.common.TestOptions;
import com.elastonias.container.util.CustomUtil;
import com.google.gson.JsonArray;

import org.elastos.carrier.exceptions.CarrierException;
import org.elastos.carrier.AbstractCarrierHandler;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.ConnectionStatus;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.PresenceStatus;

import java.io.*;
import java.lang.Thread;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.xml.crypto.Data;


public class ElastosCarrier extends Thread{
	private final static String CONTROLLER_CONNECTION_KEYWORD="connection_key";
	private static Synchronizer syncher=new Synchronizer();
	private static TestOptions options;
	private static CarrierHandler carrierHandler;
	private static Carrier carrier;
	private App app;
	private boolean isConnected=false;

	static{
		loadElastosLib();
	}

	public ElastosCarrier(App app){
		this.app=app;
		File carrierDir=CustomUtil.getDirectoryByName("carrier");
		options=new TestOptions(carrierDir.getPath());
		carrierHandler=new CarrierHandler();
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
		catch(CarrierException | InterruptedException e){}
	}
	
	public void kill(){
		carrier.kill();
	}
	
	public boolean isConnected(){
		return this.isConnected;
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

	public boolean isFriend(String userId){
		boolean response=true;
		try{
			response=carrier.isFriend(userId);
		}
		catch(CarrierException ce){
			response=false;
		}
		syncher.wakeup();
		return response;
	}
	
	public boolean acceptFriend(String userId){
		boolean response=true;
		try{
			carrier.acceptFriend(userId);
		}
		catch(CarrierException ce){
			response=false;
		}
		syncher.wakeup();
		return response;
	}

	public boolean addFriend(String address){
		boolean response=true;
		try{
			carrier.addFriend(address, CONTROLLER_CONNECTION_KEYWORD);
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
			if(status==ConnectionStatus.Connected){
				isConnected=true;
				String address=getAddress();
				String userId=getUserId();
				System.out.println("address: "+address);
				System.out.println("userId: "+userId);
				//removeFriend("5Meg6Hx5AQnDwJjFEvTGNXf2p8eRucg9n5HTzLwEgiiC");
				//removeFriend("3scgDdDxQwLnpowJb6puaSot8X4G4Wr2VsuyFEuxProq");
			}
			else{
				isConnected=false;
			}
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
			Controller controller=app.getDatabase().getControllerByUserId(friendId);
			if(status==ConnectionStatus.Connected){
				controller.setConnectionState("online");
			}
			else{
				controller.setConnectionState("offline");
			}
			app.getDatabase().updateController(controller);
			syncher.wakeup();
		}

		@Override
		public void onFriendRequest(Carrier carrier, String userId, UserInfo info, String hello){
			System.out.println("CarrierHandler - onFriendRequest - "+userId+" - "+hello);
			Controller controller=app.getDatabase().getActiveController();
			if(controller!=null){
				JsonObject jsonObject=new JsonObject();
				jsonObject.addProperty("command", "containerReceived");
				sendFriendMessage(controller.getUserId(), jsonObject.toString());
				removeFriend(controller.getUserId());
				app.getDatabase().deleteControllerByUserId(controller.getUserId());
			}

			Controller newController=new Controller(0, userId, "offline", true);
			app.getDatabase().saveController(newController);
			acceptFriend(userId);
			syncher.wakeup();
		}

		@Override
		public void onFriendAdded(Carrier carrier, FriendInfo info){
			System.out.println("CarrierHandler - onFriendAdded - "+info);
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

			SensorManagement sensorManagement=app.getSensorManagement();
			sensorManagement.setControllerUserId(from);
			Gson gson=new Gson();
			JsonObject resultObject=gson.fromJson(messageText, JsonObject.class);
			String command=resultObject.get("command").getAsString();

			if(command.equals("removeMe")){
				app.getDatabase().deleteControllerByUserId(from);
				removeFriend(from);
			}
			else if(command.equals("addProduct")){
				if(!sensorManagement.isRunning()){
					sensorManagement.start();
				}
				JsonObject productObject=resultObject.getAsJsonObject("product");
				String did=productObject.get("did").getAsString();
				String privateKey=productObject.get("privateKey").getAsString();
				String publicKey=productObject.get("publicKey").getAsString();

				Product product=app.getDatabase().getProductByDid(did);
				if(product==null){
					product=new Product(0, did, privateKey, publicKey, true);
					app.getDatabase().saveProduct(product);

					DIDObject containerDIDObject=app.getDidManagement().getDidObject();
					String strTimestamp=String.valueOf(new Date().getTime());
					JsonObject jsonObject=new JsonObject();
					jsonObject.addProperty("containerDID", containerDIDObject.getDid());
					app.getDidManagement().saveDataByProduct(product, strTimestamp, jsonObject);
				}
				else{
					product.setVisible(true);
					app.getDatabase().updateProduct(product);
				}
				app.refreshProductList();
			}
			else if(command.equals("unloadContainer")){
				sensorManagement.stop();
				app.getDatabase().setProductListVisible(false);
				app.refreshProductList();
			}
			else if(command.equals("shipContainer")){

			}
			else if(command.equals("getProducts")){
				ArrayList<Product> productList=app.getDatabase().getVisibleProductList();
				for(Product product : productList){
					JsonElement jsonProduct=gson.toJsonTree(product);
					JsonObject jsonObject=new JsonObject();
					jsonObject.addProperty("command", "addProduct");
					jsonObject.add("product", jsonProduct);
					sendFriendMessage(from, jsonObject.toString());
				}
			}
			syncher.wakeup();
		}

		@Override
		public void onFriendInviteRequest(Carrier carrier, String from, String data){
			syncher.wakeup();
		}
	}

	private static void loadElastosLib(){
		try{
			String resourcePath="/lib/";
			ArrayList<String> libList=new ArrayList<String>();
			String osName=System.getProperty("os.name");
			if(osName.contains("Linux")){
				String osArch=System.getProperty("os.arch");
				if(osArch.contains("arm")){
					resourcePath+="rpi";
				}
				else{
					resourcePath+="linux";
				}

				libList.add("libcrystal.so");
				libList.add("libelacarrier.so");
				libList.add("libelasession.so");
				libList.add("libcarrierjni.so");
				libList.add("libelafiletrans.so");

				for(int i=0;i<libList.size();i++){
					String libFile=libList.get(i);
					InputStream is=ElastosCarrier.class.getResourceAsStream(resourcePath+"/"+libFile);
					File file=new File("/usr/lib/"+libFile);
					OutputStream os=new FileOutputStream(file);
					byte[] buffer=new byte[1024];
					int length;
					while((length=is.read(buffer))!=-1){
						os.write(buffer, 0, length);
					}
					is.close();
					os.close();
				}
			}
			else if(osName.contains("Windows")){
				resourcePath+="windows";
				libList.add("crystal.dll");
				libList.add("elacarrier.dll");
				libList.add("elafiletrans.dll");
				libList.add("elasession.dll");
				libList.add("libcarrierjni.dll");
				libList.add("pthreadVC2.dll");
				libList.add("libgcc_s_seh-1.dll");
				libList.add("ucrtbased.dll");
				libList.add("vcruntime140d.dll");
                libList.add("msvcp140d.dll");

				for(int i=0;i<libList.size();i++){
					String libFile=libList.get(i);
					InputStream is=ElastosCarrier.class.getResourceAsStream(resourcePath+"/"+libFile);
					File file=new File(libFile);
					OutputStream os=new FileOutputStream(file);
					byte[] buffer=new byte[1024];
					int length;
					while((length=is.read(buffer))!=-1){
						os.write(buffer, 0, length);
					}
					is.close();
					os.close();
				}
			}
			else if(osName.contains("Mac")){
				resourcePath+="mac";
				libList.add("libcrystal.dylib");
				libList.add("libelacarrier.dylib");
				libList.add("libelasession.dylib");
				libList.add("libcarrierjni.dylib");
				libList.add("libelafiletrans.dylib");

				for(int i=0;i<libList.size();i++){
					String libFile=libList.get(i);
					InputStream is=ElastosCarrier.class.getResourceAsStream(resourcePath+"/"+libFile);
					File file=new File(libFile);
					OutputStream os=new FileOutputStream(file);
					byte[] buffer=new byte[1024];
					int length;
					while((length=is.read(buffer))!=-1){
						os.write(buffer, 0, length);
					}
					is.close();
					os.close();
				}
			}
		}
		catch(IOException ioe){}
	}
}