package com.elastonias.container.database;

import com.elastonias.container.model.Controller;
import com.elastonias.container.model.DIDObject;
import com.elastonias.container.model.Product;

import java.util.ArrayList;


public interface DatabaseInterface{

    Product saveProduct(Product product);

    ArrayList<Product> getProductList();

    ArrayList<Product> getVisibleProductList();

    Product getProductByDid(String did);

    boolean updateProduct(Product product);

    boolean setProductListVisible(boolean visible);

    boolean setControllerListDeactivated();

    Controller getActiveController();

    Controller saveController(Controller controller);

    Controller getControllerByUserId(String userId);

    boolean updateController(Controller controller);

    boolean setControllerListConnectionState(String connectionState);

    boolean deleteControllerByUserId(String userId);

    boolean deleteProducts();

    DIDObject saveDIDObject(DIDObject didObject);

    boolean deleteDIDObjects();

    DIDObject getDIDObject();

}
