package com.elastonias.container.model;

public class Controller{
    private int id;
    private String userId;
    private String connectionState; /*** online, offline ***/
    private boolean active;

    public Controller(int id, String userId, String connectionState, boolean active){
        this.id=id;
        this.userId=userId;
        this.connectionState=connectionState;
        this.active=active;
    }

    public int getId(){
        return id;
    }

    public String getUserId(){
        return userId;
    }

    public String getConnectionState(){
        return connectionState;
    }

    public boolean getActive(){
        return active;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setUserId(String userId){
        this.userId=userId;
    }

    public void setConnectionState(String connectionState){
        this.connectionState=connectionState;
    }

    public void setActive(boolean active){
        this.active=active;
    }
}
