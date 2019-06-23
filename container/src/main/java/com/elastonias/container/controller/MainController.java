package com.elastonias.container.controller;

import com.elastonias.container.App;
import com.elastonias.container.model.Product;
import com.elastonias.container.util.QRCodeUtil;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class MainController{
    private App app;
    private ObservableList<Product> productObservableList;

    @FXML private ImageView qrCodeImageView;
    @FXML private TableView productListTableView;
    @FXML private TableColumn didColumn;

    public MainController(){}

    public void setApp(App app){
        this.app=app;
    }

    public void init(){
        String address=app.getElastosCarrier().getAddress();

        Image qrCode=QRCodeUtil.getQRCodeImage(address, 170, 170);
        qrCodeImageView.setImage(qrCode);

        refreshProductList();
    }

    public void refreshProductList(){
        ArrayList<Product> productList=app.getDatabase().getVisibleProductList();
        productObservableList=FXCollections.observableArrayList(productList);
        didColumn.setCellValueFactory(new PropertyValueFactory("did"));
        productListTableView.setItems(productObservableList);
    }
}
