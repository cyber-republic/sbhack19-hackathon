package com.elastonias.container.service.elastos.blockchain;

import com.alibaba.fastjson.JSON;
import com.elastonias.container.database.DatabaseInterface;
import com.elastonias.container.model.DIDObject;
import com.elastonias.container.model.Product;
import com.google.gson.JsonObject;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.elastos.service.ElaDidService;
import org.elastos.service.ElaDidServiceImp;
import org.elastos.util.HttpUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DIDManagement{
    private final String ELA_NODE_URL="http://did-mainnet-node-lb-1452309420.ap-northeast-1.elb.amazonaws.com:20604";
    private DIDObject didObject;
    private ElaDidService didService;

    public DIDManagement(DatabaseInterface database){
        didService=new ElaDidServiceImp();
        didService.setElaNodeUrl(ELA_NODE_URL);
        didObject=database.getDIDObject();
        if(didObject==null){
            didObject=createDIDObject();
            database.saveDIDObject(didObject);

        }
    }

    public DIDObject getDidObject(){
        return didObject;
    }

    public String packDidRawData(String key, String value) {
        String rawData=didService.packDidRawData(didObject.getPrivateKey(), key, value);
        return rawData;
    }

    public Map<String, String> getHeader(){
        Map<String, String> header=new HashMap<String, String>();
        long time=new Date().getTime();
        String strTime=String.valueOf(time);
        SimpleHash hash=new SimpleHash("md5", "", strTime, 1);
        String auth = hash.toHex();
        Map<String, String> map=new HashMap<>();
        map.put("id", "unCZRceA8o7dbny");
        map.put("time", String.valueOf(time));
        map.put("auth", auth);
        String X_Elastos_Agent_Auth_value=JSON.toJSONString(map);
        header.put("X-Elastos-Agent-Auth", X_Elastos_Agent_Auth_value);
        return header;
    }

    public void saveData(String key, JsonObject jsonObject){
        System.out.println("saveData");
        String rawData=packDidRawData(key, jsonObject.toString());
        Map<String, String> header=getHeader();
        String response=HttpUtil.post("https://api-wallet-did.elastos.org/api/1/blockagent/upchain/data", rawData, header);
    }

    public void saveDataByProduct(Product product, String key, JsonObject jsonObject){
        String rawData=didService.packDidRawData(product.getPrivateKey(), key, jsonObject.toString());
        Map<String, String> header=getHeader();
        String response=HttpUtil.post("https://api-wallet-did.elastos.org/api/1/blockagent/upchain/data", rawData, header);
    }

    public ArrayList<JsonObject> getDataList(){
        return null;
    }

    private DIDObject createDIDObject(){
        String ret=didService.createDid();
        Map data=JSON.parseObject(ret, Map.class);
        String didPrivateKey=(String)data.get("DidPrivateKey");
        String did=(String)data.get("DID");
        String didPublicKey=(String) data.get("DidPublicKey");
        DIDObject newDidobject=new DIDObject(0, did, didPublicKey, didPrivateKey);
        return newDidobject;
    }
}
