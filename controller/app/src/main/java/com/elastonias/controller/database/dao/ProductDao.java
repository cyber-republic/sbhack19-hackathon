package com.elastonias.controller.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.database.entity.Product;

import java.util.List;


@Dao
public interface ProductDao{

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    void insert(Product product);

    @Update(onConflict=OnConflictStrategy.REPLACE)
    void update(Product product);

    @Query("SELECT * FROM product_table WHERE containerId = :containerId AND visible = 1")
    LiveData<List<Product>> getLiveProductListByContainerId(int containerId);

    @Query("SELECT * FROM product_table WHERE did = :did AND containerId = :containerId")
    Product getProductByDidAndContainerId(String did, int containerId);

    @Query("SELECT * FROM product_table WHERE did = :did AND active = 0")
    Product getUnusedProductByDid(String did);

    @Query("SELECT * FROM product_table WHERE did = :did")
    Product getProductByDid(String did);

    @Query("DELETE FROM product_table WHERE containerId = :containerId")
    void deleteProductsByContainerId(int containerId);

    @Query("DELETE FROM product_table")
    void deleteAllProducts();

    @Query("UPDATE product_table SET visible = :visible WHERE containerId = :containerId;")
    void setProductListVisibleByContainerId(boolean visible, int containerId);
}

