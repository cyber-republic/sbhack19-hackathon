package com.elastonias.controller.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;


@Entity(tableName="container_table")
public class Container{

    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    private int id;

    @NonNull
    @ColumnInfo(name="userId")
    private String userId;

    @NonNull
    @ColumnInfo(name="address")
    private String address;

    @NonNull
    @ColumnInfo(name="name")
    private String name;

    @NonNull
    @ColumnInfo(name="state")
    private String state; /*** active, pending ***/

    @NonNull
    @ColumnInfo(name="connectionState")
    private String connectionState; /*** online, offline ***/

    @NonNull
    @ColumnInfo(name="processState")
    private String processState; /*** sealed, unsealed, transport, delivered ***/

    public Container(int id, @NonNull String userId, @NonNull String address, @NonNull String name, @NonNull String state, @NonNull String connectionState, @NonNull String processState){
        this.id=id;
        this.userId=userId;
        this.address=address;
        this.name=name;
        this.state=state;
        this.connectionState=connectionState;
        this.processState=processState;
    }

    public int getId(){
        return id;
    }

    @NonNull
    public String getUserId(){
        return userId;
    }

    @NonNull
    public String getAddress(){
        return address;
    }

    @NonNull
    public String getName(){
        return name;
    }

    @NonNull
    public String getState(){
        return state;
    }

    @NonNull
    public String getConnectionState(){
        return connectionState;
    }

    @NonNull
    public String getProcessState(){
        return processState;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setUserId(@NonNull String userId){
        this.userId=userId;
    }

    public void setAddress(@NonNull String address){
        this.address=address;
    }

    public void setName(@NonNull String name){
        this.name=name;
    }

    public void setState(@NonNull String state){
        this.state=state;
    }

    public void setConnectionState(@NonNull String connectionState){
        this.connectionState=connectionState;
    }

    public void setProcessState(@NonNull String processState){
        this.processState=processState;
    }
}
