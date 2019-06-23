package com.elastonias.container.model;

public class DIDObject{
    private int id;
    private String did;
    private String publicKey;
    private String privateKey;

    public DIDObject(int id, String did, String publicKey, String privateKey){
        this.id=id;
        this.did=did;
        this.publicKey=publicKey;
        this.privateKey=privateKey;
    }

    public int getId(){
        return id;
    }

    public String getDid(){
        return did;
    }

    public String getPublicKey(){
        return publicKey;
    }

    public String getPrivateKey(){
        return privateKey;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setDid(String did){
        this.did=did;
    }

    public void setPublicKey(String publicKey){
        this.publicKey=publicKey;
    }

    public void setPrivateKey(String privateKey){
        this.privateKey=privateKey;
    }
}
