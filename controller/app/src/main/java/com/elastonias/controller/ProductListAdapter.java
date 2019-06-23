package com.elastonias.controller;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.database.entity.Product;
import com.elastonias.controller.service.GlobalApplication;
import com.elastonias.controller.service.LocalRepository;
import com.elastonias.controller.service.elastos.ElastosCarrier;

import java.util.List;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>{
    private final LayoutInflater inflater;
    private final Context appContext;
    private List<Product> productList;
    private static ElastosCarrier elastosCarrier;
    private static LocalRepository localRepository;

    public ProductListAdapter(Context context){
        inflater=LayoutInflater.from(context);
        appContext=context;
        elastosCarrier=GlobalApplication.getElastosCarrier();
        localRepository=GlobalApplication.getLocalRepository();
    }

    public void setProductList(List<Product> newProductList){
        productList=newProductList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View itemView=inflater.inflate(R.layout.item_recyclerview_products, viewGroup, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i){
        if(productList!=null){
            Product product=productList.get(i);
            productViewHolder.bind(product);
        }
    }

    @Override
    public int getItemCount(){
        int count=0;
        if(productList!=null){
            count=productList.size();
        }
        return count;
    }

    class ProductViewHolder extends RecyclerView.ViewHolder{
        private TextView productName;

        ProductViewHolder(View itemView){
            super(itemView);
            productName=itemView.findViewById(R.id.productName);
        }

        void bind(Product product){
            productName.setText(product.getDid());
        }
    }


}
