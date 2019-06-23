package com.elastonias.controller;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.database.entity.Product;
import com.elastonias.controller.service.GlobalApplication;
import com.elastonias.controller.service.LocalRepository;
import com.elastonias.controller.service.elastos.ElastosCarrier;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.elastonias.controller.MainActivity.PERMISSION_REQUEST_CODE_CAMERA;

public class LoadContainerActivity extends AppCompatActivity{
    private AppCompatButton backButton;
    private DecoratedBarcodeView barcodeView;
    private RecyclerView recyclerView;
    private ProductListAdapter productListAdapter;
    private ProductsViewModel productsViewModel;
    private LinearLayout emptyPlaceholder;
    private AppCompatButton finishButton;
    private int containerId;
    private String containerUserId;
    private String tempProductDid="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_container);

        containerId=getIntent().getIntExtra("containerId", -1);
        containerUserId=getIntent().getStringExtra("containerUserId");
        LocalRepository localRepository=GlobalApplication.getLocalRepository();

        barcodeView=findViewById(R.id.barcodeView);
        openCamera();

        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(buttonView -> {
            stopCamera();
            setResult(RESULT_CANCELED);
            finish();
        });

        emptyPlaceholder=findViewById(R.id.emptyPlaceholder);
        recyclerView=findViewById(R.id.products_recyclerview);
        productListAdapter=new ProductListAdapter(getApplicationContext());
        recyclerView.setAdapter(productListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        productsViewModel=ViewModelProviders.of(this).get(ProductsViewModel.class);
        productsViewModel.getLiveProductListByContainerId(containerId).observe(this, productList -> {
            productListAdapter.setProductList(productList);
            if(productList==null || productList.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                emptyPlaceholder.setVisibility(View.VISIBLE);
            }
            else{
                recyclerView.setVisibility(View.VISIBLE);
                emptyPlaceholder.setVisibility(View.GONE);
            }
        });

        finishButton=findViewById(R.id.finishButton);
        finishButton.setOnClickListener(buttonView -> {
            localRepository.updateContainerProcessStateByContainerId("sealed", containerId);
            stopCamera();
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void openCamera(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestCameraPermission();
        }
        else{
            startCamera();
        }
    }

    private void requestCameraPermission(){
        requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch(requestCode){
            case PERMISSION_REQUEST_CODE_CAMERA:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startCamera();
                }
                else{
                    //permission denied, do nothing
                }
                return;
            }
        }
    }

    private void startCamera(){
        BarcodeCallback callback=new BarcodeCallback(){
            @Override
            public void barcodeResult(BarcodeResult result){
                String productDid=result.getText();
                addProduct(productDid);
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints){}
        };

        Collection<BarcodeFormat> formats=Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(this.getIntent());
        barcodeView.decodeContinuous(callback);
        barcodeView.resume();
    }

    public void stopCamera(){
        barcodeView.pause();
    }

    private void addProduct(String productDid){
        ElastosCarrier elastosCarrier=GlobalApplication.getElastosCarrier();
        LocalRepository localRepository=GlobalApplication.getLocalRepository();

        if(productDid.isEmpty()){
            Snackbar.make(backButton, "Product DID is empty.", Snackbar.LENGTH_SHORT).show();
        }
        else{
            if(!tempProductDid.equals(productDid)){
                Product product=localRepository.getProductByDid(productDid);
                if(product==null){
                    Snackbar.make(backButton, "Product is not valid.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    product.setContainerId(containerId);
                    product.setActive(true);
                    product.setVisible(true);
                    localRepository.updateProduct(product);
                    JsonElement jsonProduct=new Gson().toJsonTree(product);
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("command", "addProduct");
                    jsonObject.add("product", jsonProduct);
                    elastosCarrier.sendFriendMessage(containerUserId, jsonObject.toString());
                    Snackbar.make(backButton, "Product has been added.", Snackbar.LENGTH_SHORT).show();
                }
            }
            tempProductDid=productDid;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopCamera();
    }
}
