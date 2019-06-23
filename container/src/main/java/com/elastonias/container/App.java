package com.elastonias.container;

import com.elastonias.container.controller.MainController;
import com.elastonias.container.database.DatabaseInterface;
import com.elastonias.container.database.DatabaseSQLite;
import com.elastonias.container.management.SensorManagement;
import com.elastonias.container.model.DIDObject;
import com.elastonias.container.service.elastos.ElastosCarrier;
import com.elastonias.container.service.elastos.blockchain.DIDManagement;
import com.google.gson.JsonObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application{
    private static ElastosCarrier elastosCarrier;
    private DatabaseInterface database;
    private DIDManagement didManagement;
    private SensorManagement sensorManagement;
    private Stage primaryStage;
    private AnchorPane mainScene;
    private MainController mainController;

    @Override
    public void start(Stage primaryStage){
        elastosCarrier=new ElastosCarrier(this);
        elastosCarrier.start();

        database=new DatabaseSQLite("local.db");
        database.setControllerListConnectionState("offline");
        didManagement=new DIDManagement(database);
        sensorManagement=new SensorManagement(elastosCarrier, didManagement);

        this.primaryStage=primaryStage;
        this.primaryStage.setTitle("Container");
        this.primaryStage.setMinWidth(680);
        this.primaryStage.setMinHeight(520);

        initMainLayout();
    }

    @Override
    public void stop(){
        elastosCarrier.kill();
        Platform.exit();
    }

    public ElastosCarrier getElastosCarrier(){
        return elastosCarrier;
    }

    public DatabaseInterface getDatabase(){
        return database;
    }

    public DIDManagement getDidManagement(){
        return didManagement;
    }

    public SensorManagement getSensorManagement(){
        return sensorManagement;
    }

    public void initMainLayout(){
        try{
            FXMLLoader loader=new FXMLLoader(getClass().getClassLoader().getResource("scenes/main.fxml"));
            mainScene=(AnchorPane)loader.load();
            Scene scene=new Scene(mainScene);
            primaryStage.setScene(scene);
            mainController=loader.getController();
            mainController.setApp(this);
            mainController.init();
            primaryStage.show();
        }
        catch(IOException ioe){}
    }

    public void refreshProductList(){
        mainController.refreshProductList();
    }

    public static void main(String[] arguments){
        launch(arguments);
    }
}
