package com.elastonias.controller;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;




public class MainFragment extends Fragment{
    private RecyclerView recyclerView;
    private ContainerListAdapter containerListAdapter;
    private ContainersViewModel containersViewModel;
    private LinearLayout emptyPlaceholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fragment_main, container, false);

        emptyPlaceholder=view.findViewById(R.id.emptyPlaceholder);
        recyclerView=view.findViewById(R.id.containers_recyclerview);
        containerListAdapter=new ContainerListAdapter(getContext(), this);
        recyclerView.setAdapter(containerListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        containersViewModel=ViewModelProviders.of(this).get(ContainersViewModel.class);
        containersViewModel.getLiveContainerList().observe(this, containerList -> {
            containerListAdapter.setContainerList(containerList);
            if(containerList==null || containerList.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                emptyPlaceholder.setVisibility(View.VISIBLE);
            }
            else{
                recyclerView.setVisibility(View.VISIBLE);
                emptyPlaceholder.setVisibility(View.GONE);
            }
        });

        return view;
    }

}

