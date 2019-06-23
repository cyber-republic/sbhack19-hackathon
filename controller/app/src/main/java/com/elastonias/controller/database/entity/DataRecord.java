package com.elastonias.controller.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName="data_record_table")
public class DataRecord{
    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    private int id;

    @ColumnInfo(name="timestamp")
    private long timestamp;

    @ColumnInfo(name="value")
    private int value;

    @NonNull
    @ColumnInfo(name="containerUserId")
    private String containerUserId;

    public DataRecord(int id, long timestamp, int value, @NonNull String containerUserId){
        this.id=id;
        this.timestamp=timestamp;
        this.value=value;
        this.containerUserId=containerUserId;
    }

    public int getId(){
        return id;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public int getValue(){
        return value;
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

    public void setValue(int value){
        this.value=value;
    }

    public void setContainerUserId(@NonNull String containerUserId){
        this.containerUserId=containerUserId;
    }
}
