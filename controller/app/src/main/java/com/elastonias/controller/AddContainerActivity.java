package com.elastonias.controller;

import static com.elastonias.controller.MainActivity.PERMISSION_REQUEST_CODE_CAMERA;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;

import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.service.GlobalApplication;
import com.elastonias.controller.service.LocalRepository;
import com.elastonias.controller.service.elastos.ElastosCarrier;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class AddContainerActivity extends AppCompatActivity{
    private AppCompatButton backButton;
    private TextInputEditText containerNameInput;
    //private TextInputEditText containerAddressInput;
    //private AppCompatButton finishButton;
    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_container);

        barcodeView=findViewById(R.id.barcodeView);
        openCamera();

        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(buttonView -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        containerNameInput=findViewById(R.id.containerNameInput);
        //containerAddressInput=findViewById(R.id.containerAddressInput);

        /*finishButton=findViewById(R.id.finishButton);
        finishButton.setOnClickListener(view -> {
            String containerName=containerNameInput.getText().toString();
            String containerAddress=containerAddressInput.getText().toString();
            if(containerName.isEmpty()){
                Snackbar.make(finishButton, "Container name is empty.", Snackbar.LENGTH_SHORT).show();
            }
            else if(containerAddress.isEmpty()){
                Snackbar.make(finishButton, "Container address is empty.", Snackbar.LENGTH_SHORT).show();
            }
            else{
                addContainer(containerAddress);
            }
        });*/
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
                String containerAddress=result.getText();
                addContainer(containerAddress);
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

    private void addContainer(String containerAddress){
        ElastosCarrier elastosCarrier=GlobalApplication.getElastosCarrier();
        LocalRepository localRepository=GlobalApplication.getLocalRepository();
        String containerName=containerNameInput.getText().toString();

        if(containerName.isEmpty()){
            Snackbar.make(backButton, "Container name is empty.", Snackbar.LENGTH_SHORT).show();
        }
        else{
            Container container=localRepository.getContainerByAddress(containerAddress);
            if(container==null){
                boolean addressCheck=elastosCarrier.isValidAddress(containerAddress);
                if(addressCheck){
                    Container newContainer=new Container(0, "undefined", containerAddress, containerName, "pending", "offline", "unsealed");
                    boolean addCheck=elastosCarrier.addFriend(newContainer);
                    if(addCheck){
                        stopCamera();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else{
                        Snackbar.make(barcodeView, "Sorry, something went wrong.", Snackbar.LENGTH_SHORT).show();
                    }
                }
                else{
                    Snackbar.make(barcodeView, "Container address is not valid.", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }
}
