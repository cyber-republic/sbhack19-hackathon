package com.elastonias.controller;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.service.GlobalApplication;
import com.elastonias.controller.service.LocalRepository;
import com.elastonias.controller.service.elastos.ElastosCarrier;
import com.google.gson.JsonObject;

import java.util.List;


public class ContainerListAdapter extends RecyclerView.Adapter<ContainerListAdapter.ContainerViewHolder>{
    private final LayoutInflater inflater;
    private final Context appContext;
    private List<Container> containerList;
    private static ElastosCarrier elastosCarrier;
    private static LocalRepository localRepository;
    private MainFragment mainFragment;

    public ContainerListAdapter(Context context, MainFragment mainFragment){
        inflater=LayoutInflater.from(context);
        appContext=context;
        elastosCarrier=GlobalApplication.getElastosCarrier();
        localRepository=GlobalApplication.getLocalRepository();
        this.mainFragment=mainFragment;
    }

    public void setContainerList(List<Container> newContainerList){
        containerList=newContainerList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContainerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View itemView=inflater.inflate(R.layout.item_recyclerview_containers, viewGroup, false);
        return new ContainerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContainerViewHolder containerViewHolder, int i){
        if(containerList!=null){
            Container container=containerList.get(i);
            containerViewHolder.bind(container);
        }
    }

    @Override
    public int getItemCount(){
        int count=0;
        if(containerList!=null){
            count=containerList.size();
        }
        return count;
    }

    class ContainerViewHolder extends RecyclerView.ViewHolder{
        private TextView containerName;
        private TextView containerState;
        private AppCompatImageButton loadButton;
        private AppCompatImageButton terminateButton;
        private AppCompatImageButton unloadButton;
        private AppCompatImageButton transportButton;
        private LinearLayout transportLayout;
        private LinearLayout doneLayout;
        private ConstraintLayout cardLayout;

        ContainerViewHolder(View itemView){
            super(itemView);
            containerName=itemView.findViewById(R.id.containerName);
            containerState=itemView.findViewById(R.id.containerState);
            loadButton=itemView.findViewById(R.id.loadButton);
            terminateButton=itemView.findViewById(R.id.terminateButton);
            unloadButton=itemView.findViewById(R.id.unloadButton);
            transportButton=itemView.findViewById(R.id.transportButton);
            transportLayout=itemView.findViewById(R.id.transportLayout);
            doneLayout=itemView.findViewById(R.id.doneLayout);
            cardLayout=itemView.findViewById(R.id.cardLayout);
        }

        void bind(Container container){
            containerName.setText(container.getName());

            loadButton.setVisibility(View.GONE);
            terminateButton.setVisibility(View.GONE);
            unloadButton.setVisibility(View.GONE);
            transportButton.setVisibility(View.GONE);
            transportLayout.setVisibility(View.GONE);
            doneLayout.setVisibility(View.GONE);

            if(container.getState().equals("active")){
                if(container.getProcessState().equals("sealed")){
                    containerState.setText("Sealed");
                    unloadButton.setVisibility(View.VISIBLE);
                    transportButton.setVisibility(View.VISIBLE);
                }
                else if(container.getProcessState().equals("unsealed")){
                    containerState.setText("Unsealed");
                    loadButton.setVisibility(View.VISIBLE);
                    terminateButton.setVisibility(View.VISIBLE);
                }
                else if(container.getProcessState().equals("transport")){
                    containerState.setText("Transport");
                    transportLayout.setVisibility(View.VISIBLE);
                }
                else if(container.getProcessState().equals("delivered")){
                    containerState.setText("Delivered");
                    doneLayout.setVisibility(View.VISIBLE);
                }
            }
            else{
                containerState.setText("Pending");
            }

            loadButton.setOnClickListener(buttonView -> {
                if(container.getConnectionState().equals("online")){
                    Intent intent=new Intent(appContext, LoadContainerActivity.class);
                    intent.putExtra("containerId", container.getId());
                    intent.putExtra("containerUserId", container.getUserId());
                    appContext.startActivity(intent);
                }
                else{
                    Snackbar.make(loadButton, "Container is offline.", Snackbar.LENGTH_SHORT).show();
                }
            });

            terminateButton.setOnClickListener(buttonView -> {
                if(container.getConnectionState().equals("online")){
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("command", "removeMe");
                    elastosCarrier.sendFriendMessage(container.getUserId(), jsonObject.toString());
                    elastosCarrier.removeFriend(container.getUserId());
                    localRepository.deleteContainerById(container.getId());
                    Snackbar.make(loadButton, "Container has been terminated.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    Snackbar.make(loadButton, "Container is offline.", Snackbar.LENGTH_SHORT).show();
                }
            });

            unloadButton.setOnClickListener(buttonView -> {
                if(container.getConnectionState().equals("online")){
                    container.setProcessState("unsealed");
                    localRepository.updateContainer(container);
                    localRepository.setProductListVisibleByContainerId(false, container.getId());
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("command", "unloadContainer");
                    elastosCarrier.sendFriendMessage(container.getUserId(), jsonObject.toString());
                    Snackbar.make(loadButton, "Container has been unsealed.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    Snackbar.make(loadButton, "Container disconnected.", Snackbar.LENGTH_SHORT).show();
                }
            });

            transportButton.setOnClickListener(buttonView -> {
                if(container.getConnectionState().equals("online")){
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("command", "shipContainer");
                    elastosCarrier.sendFriendMessage(container.getUserId(), jsonObject.toString());
                    localRepository.updateContainerProcessStateByContainerId("transport", container.getId());
                    Snackbar.make(loadButton, "Container has been shipped.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    Snackbar.make(loadButton, "Container is offline.", Snackbar.LENGTH_SHORT).show();
                }
            });

            cardLayout.setOnClickListener(cardView -> {
                Intent intent=new Intent(appContext, ContainerProductsActivity.class);
                intent.putExtra("containerId", container.getId());
                intent.putExtra("containerUserId", container.getUserId());
                appContext.startActivity(intent);
            });
        }
    }


}
