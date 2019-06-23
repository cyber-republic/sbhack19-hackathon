package com.elastonias.controller;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

public class ContainerProductsActivity extends AppCompatActivity{
    private AppCompatButton backButton;
    private RecyclerView recyclerView;
    private ProductListAdapter productListAdapter;
    private ProductsViewModel productsViewModel;
    private LinearLayout emptyPlaceholder;
    private LinearLayout productLayout;
    private int containerId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_products);

        containerId=getIntent().getIntExtra("containerId", -1);

        backButton=findViewById(R.id.backButton);
        backButton.setOnClickListener(buttonView -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        emptyPlaceholder=findViewById(R.id.emptyPlaceholder);
        productLayout=findViewById(R.id.productLayout);
        recyclerView=findViewById(R.id.products_recyclerview);
        productListAdapter=new ProductListAdapter(getApplicationContext());
        recyclerView.setAdapter(productListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        productsViewModel=ViewModelProviders.of(this).get(ProductsViewModel.class);
        productsViewModel.getLiveProductListByContainerId(containerId).observe(this, productList -> {
            productListAdapter.setProductList(productList);
            if(productList==null || productList.isEmpty()){
                productLayout.setVisibility(View.GONE);
                emptyPlaceholder.setVisibility(View.VISIBLE);
            }
            else{
                productLayout.setVisibility(View.VISIBLE);
                emptyPlaceholder.setVisibility(View.GONE);
            }
        });
    }
}
