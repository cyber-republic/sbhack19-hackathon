package com.elastonias.controller;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.service.GlobalApplication;
import com.elastonias.controller.service.LocalRepository;

import java.util.List;


public class ContainersViewModel extends AndroidViewModel{
    private LocalRepository localRepository;

    public ContainersViewModel(Application application){
        super(application);
        localRepository=GlobalApplication.getLocalRepository();
    }

    public LiveData<List<Container>> getLiveContainerList(){
        return localRepository.getLiveContainerList();
    }

    public LiveData<Container> getLiveContainerById(int id){
        return localRepository.getLiveContainerById(id);
    }
}
