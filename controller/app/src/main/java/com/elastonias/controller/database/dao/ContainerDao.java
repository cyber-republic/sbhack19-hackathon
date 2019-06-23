package com.elastonias.controller.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.elastonias.controller.database.entity.Container;
import java.util.List;


@Dao
public interface ContainerDao{

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insert(Container device);

    @Update(onConflict=OnConflictStrategy.REPLACE)
    void update(Container device);

    @Query("SELECT * FROM container_table")
    List<Container> getContainerList();

    @Query("SELECT * FROM container_table")
    LiveData<List<Container>> getLiveContainerList();

    @Query("SELECT * FROM container_table WHERE id = :id")
    Container getContainerById(int id);

    @Query("SELECT * FROM container_table WHERE userId = :userId")
    Container getContainerByUserId(String userId);

    @Query("SELECT * FROM container_table WHERE address = :address")
    Container getContainerByAddress(String address);

    @Query("SELECT * FROM container_table WHERE id = :id")
    LiveData<Container> getLiveContainerById(int id);

    @Query("SELECT * FROM container_table WHERE userId = :userId")
    LiveData<Container> getLiveContainerByUserId(String userId);

    @Query("UPDATE container_table SET processState = :processState WHERE id = :containerId")
    void updateContainerProcessStateByContainerId(String processState, int containerId);

    @Query("UPDATE container_table SET connectionState = :connectionState;")
    void setContainerListConnectionState(String connectionState);

    @Query("DELETE FROM container_table WHERE id = :id")
    void deleteContainerById(int id);

}

