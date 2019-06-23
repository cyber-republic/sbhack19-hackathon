package com.elastonias.controller.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName="event_table")
public class Event{
    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    private int id;

    @ColumnInfo(name="timestamp")
    private long timestamp;

    @NonNull
    @ColumnInfo(name="condition")
    private String condition;

    @ColumnInfo(name="value")
    private int value;

    @NonNull
    @ColumnInfo(name="notification")
    private String notification;

    @NonNull
    @ColumnInfo(name="containerUserId")
    private String containerUserId;

    public Event(int id, long timestamp, @NonNull String condition, int value, @NonNull String notification, @NonNull String containerUserId){
        this.id=id;
        this.timestamp=timestamp;
        this.condition=condition;
        this.value=value;
        this.notification=notification;
        this.containerUserId=containerUserId;
    }

    public int getId(){
        return id;
    }

    public long getTimestamp(){
        return timestamp;
    }

    @NonNull
    public String getCondition(){
        return condition;
    }

    public int getValue(){
        return value;
    }

    @NonNull
    public String getNotification(){
        return notification;
    }

    @NonNull
    public String getContainerUserId(){
        return containerUserId;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setTimestamp(long timestamp){
        this.timestamp=timestamp;
    }

    public void setCondition(@NonNull String condition){
        this.condition=condition;
    }

    public void setValue(int value){
        this.value=value;
    }

    public void setNotification(@NonNull String notification){
        this.notification=notification;
    }

    public void setContainerUserId(@NonNull String containerUserId){
        this.containerUserId=containerUserId;
    }
}
