package com.elastonias.controller.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.database.entity.DataRecord;
import com.elastonias.controller.database.entity.Product;

import java.util.List;


@Dao
public interface DataRecordDao{

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insert(DataRecord dataRecord);

    @Update(onConflict=OnConflictStrategy.REPLACE)
    void update(DataRecord dataRecord);

    @Query("SELECT * FROM data_record_table WHERE containerUserId = :containerUserId")
    DataRecord getDataRecordByControllerUserId(String containerUserId);

    @Query("SELECT * FROM data_record_table WHERE containerUserId = :containerUserId")
    LiveData<DataRecord> getLiveDataRecordByControllerUserId(String containerUserId);

}