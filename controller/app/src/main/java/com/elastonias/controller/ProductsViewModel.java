package com.elastonias.controller;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.database.entity.Product;
import com.elastonias.controller.service.GlobalApplication;
import com.elastonias.controller.service.LocalRepository;

import java.util.List;


public class ProductsViewModel extends AndroidViewModel{
    private LocalRepository localRepository;

    public ProductsViewModel(Application application){
        super(application);
        localRepository=GlobalApplication.getLocalRepository();
    }

    public LiveData<List<Product>> getLiveProductListByContainerId(int containerId){
        return localRepository.getLiveProductListByContainerId(containerId);
    }

    public LiveData<Container> getLiveContainerById(int id){
        return localRepository.getLiveContainerById(id);
    }
}
