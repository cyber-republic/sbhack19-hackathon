package com.elastonias.controller.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;


@Entity(tableName="product_table")
public class Product{

    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    private int id;

    @NonNull
    @ColumnInfo(name="did")
    private String did;

    @NonNull
    @ColumnInfo(name="privateKey")
    private String privateKey;

    @NonNull
    @ColumnInfo(name="publicKey")
    private String publicKey;

    @ColumnInfo(name="containerId")
    private int containerId;

    @ColumnInfo(name="visible")
    private boolean visible;

    @ColumnInfo(name="active")
    private boolean active;

    public Product(int id, @NonNull String did, @NonNull String privateKey, @NonNull String publicKey, int containerId, boolean visible, boolean active){
        this.id=id;
        this.did=did;
        this.privateKey=privateKey;
        this.publicKey=publicKey;
        this.containerId=containerId;
        this.visible=visible;
        this.active=active;
    }

    public int getId(){
        return id;
    }

    @NonNull
    public String getDid(){
        return did;
    }

    @NonNull
    public String getPrivateKey(){
        return privateKey;
    }

    @NonNull
    public String getPublicKey(){
        return publicKey;
    }

    public int getContainerId(){
        return containerId;
    }

    public boolean isVisible(){
        return visible;
    }

    public boolean isActive(){
        return active;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setDid(@NonNull String did){
        this.did=did;
    }

    public void setPrivateKey(@NonNull String privateKey){
        this.privateKey=privateKey;
    }

    public void setPublicKey(@NonNull String publicKey){
        this.publicKey=publicKey;
    }

    public void setContainerId(int containerId){
        this.containerId=containerId;
    }

    public void setVisible(boolean visible){
        this.visible=visible;
    }

    public void setActive(boolean active){
        this.active=active;
    }
}
