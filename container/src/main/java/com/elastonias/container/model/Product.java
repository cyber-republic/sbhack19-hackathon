package com.elastonias.container.model;

public class Product{
    private int id;
    private String did;
    private String privateKey;
    private String publicKey;
    private boolean visible;

    public Product(int id, String did, String privateKey, String publicKey, boolean visible){
        this.id=id;
        this.did=did;
        this.privateKey=privateKey;
        this.publicKey=publicKey;
        this.visible=visible;
    }

    public int getId(){
        return id;
    }

    public String getDid(){
        return did;
    }

    public String getPrivateKey(){
        return privateKey;
    }

    public String getPublicKey(){
        return publicKey;
    }

    public boolean getVisible(){
        return visible;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setDid(String did){
        this.did=did;
    }

    public void setPrivateKey(String privateKey){
        this.privateKey=privateKey;
    }

    public void setPublicKey(String publicKey){
        this.publicKey=publicKey;
    }

    public void setVisible(boolean visible){
        this.visible=visible;
    }
}
