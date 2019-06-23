package com.elastonias.controller.service;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.elastonias.controller.database.LocalRoomDatabase;
import com.elastonias.controller.database.dao.ContainerDao;
import com.elastonias.controller.database.dao.DataRecordDao;
import com.elastonias.controller.database.dao.EventDao;
import com.elastonias.controller.database.dao.ProductDao;
import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.database.entity.DataRecord;
import com.elastonias.controller.database.entity.Event;
import com.elastonias.controller.database.entity.Product;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocalRepository{
    private ContainerDao containerDao;
    private ProductDao productDao;
    private DataRecordDao dataRecordDao;
    private EventDao eventDao;
    private Container tempContainer;

    public LocalRepository(Context context){
        LocalRoomDatabase localRoomDatabase=LocalRoomDatabase.getDatabase(context);
        containerDao=localRoomDatabase.containerDao();
        productDao=localRoomDatabase.productDao();
        dataRecordDao=localRoomDatabase.dataRecordDao();
        eventDao=localRoomDatabase.eventDao();
    }

    public Container getTempContainer(){
        return tempContainer;
    }

    public void setTempContainer(Container tempContainer){
        this.tempContainer=tempContainer;
    }

    public List<Container> getContainerList(){
        return containerDao.getContainerList();
    }

    public LiveData<List<Container>> getLiveContainerList(){
        return containerDao.getLiveContainerList();
    }

    public Container getContainerById(int id){
        return containerDao.getContainerById(id);
    }

    public LiveData<Container> getLiveContainerById(int id){
        return containerDao.getLiveContainerById(id);
    }

    public Container getContainerByAddress(String address){
        Container container=null;
        try{
            container=new GetContainerByAddressAsyncTask(containerDao).execute(address).get();
        }
        catch(ExecutionException e){}
        catch(InterruptedException e){}
        return container;
    }

    private static class GetContainerByAddressAsyncTask extends AsyncTask<String, Void, Container>{
        private ContainerDao containerDao;

        private GetContainerByAddressAsyncTask(ContainerDao containerDao){
            this.containerDao=containerDao;
        }

        @Override
        protected Container doInBackground(String... params){
            String address=params[0];
            Container container=containerDao.getContainerByAddress(address);
            return container;
        }
    }

    public Container getContainerByUserId(String userId){
        return containerDao.getContainerByUserId(userId);
    }

    public void insertContainer(Container container){
        new InsertContainerAsyncTask(containerDao).execute(container);
    }

    private static class InsertContainerAsyncTask extends AsyncTask<Container, Void, Void>{
        private ContainerDao containerDao;

        private InsertContainerAsyncTask(ContainerDao containerDao){
            this.containerDao=containerDao;
        }

        @Override
        protected Void doInBackground(Container... params){
            Container container=params[0];
            containerDao.insert(container);
            return null;
        }
    }

    public void updateContainer(Container container){
        new UpdateContainerAsyncTask(containerDao).execute(container);
    }

    private static class UpdateContainerAsyncTask extends AsyncTask<Container, Void, Void>{
        private ContainerDao containerDao;

        private UpdateContainerAsyncTask(ContainerDao containerDao){
            this.containerDao=containerDao;
        }

        @Override
        protected Void doInBackground(Container... params){
            Container container=params[0];
            containerDao.update(container);
            return null;
        }
    }

    public void deleteContainerById(int id){
        new DeleteContainerByIdAsyncTask(containerDao).execute(id);
    }

    private static class DeleteContainerByIdAsyncTask extends AsyncTask<Integer, Void, Void>{
        private ContainerDao containerDao;

        private DeleteContainerByIdAsyncTask(ContainerDao containerDao){
            this.containerDao=containerDao;
        }

        @Override
        protected Void doInBackground(Integer... params){
            int id=params[0];
            containerDao.deleteContainerById(id);
            return null;
        }
    }

    public Product getProductByDid(String did){
        Product product=null;
        try{
            product=new getProductByDidAsyncTask(productDao).execute(did).get();
        }
        catch(ExecutionException e){}
        catch(InterruptedException e){}
        return product;
    }

    private static class getProductByDidAsyncTask extends AsyncTask<String, Void, Product>{
        private ProductDao productDao;

        private getProductByDidAsyncTask(ProductDao productDao){
            this.productDao=productDao;
        }

        @Override
        protected Product doInBackground(String... params){
            String did=params[0];
            Product product=productDao.getProductByDid(did);
            return product;
        }
    }

    public void insertProduct(Product product){
        new InsertProductAsyncTask(productDao).execute(product);
    }

    private static class InsertProductAsyncTask extends AsyncTask<Product, Void, Void>{
        private ProductDao productDao;

        private InsertProductAsyncTask(ProductDao productDao){
            this.productDao=productDao;
        }

        @Override
        protected Void doInBackground(Product... params){
            Product product=params[0];
            productDao.insert(product);
            return null;
        }
    }

    public void updateProduct(Product product){
        new UpdateProductAsyncTask(productDao).execute(product);
    }

    private static class UpdateProductAsyncTask extends AsyncTask<Product, Void, Void>{
        private ProductDao productDao;

        private UpdateProductAsyncTask(ProductDao productDao){
            this.productDao=productDao;
        }

        @Override
        protected Void doInBackground(Product... params){
            Product product=params[0];
            productDao.update(product);
            return null;
        }
    }

    public void deleteProductsByContainerId(int containerId){
        new DeleteProductsByContainerIdAsyncTask(productDao).execute(containerId);
    }

    private static class DeleteProductsByContainerIdAsyncTask extends AsyncTask<Integer, Void, Void>{
        private ProductDao productDao;

        private DeleteProductsByContainerIdAsyncTask(ProductDao productDao){
            this.productDao=productDao;
        }

        @Override
        protected Void doInBackground(Integer... params){
            int containerId=params[0];
            productDao.deleteProductsByContainerId(containerId);
            return null;
        }
    }

    public LiveData<List<Product>> getLiveProductListByContainerId(int containerId){
        return productDao.getLiveProductListByContainerId(containerId);
    }

    public void updateContainerProcessStateByContainerId(String processState, int containerId){
        new UpdateContainerProcessStateAsyncTask(containerDao).execute(processState, containerId);
    }

    private static class UpdateContainerProcessStateAsyncTask extends AsyncTask<Object, Void, Void>{
        private ContainerDao containerDao;

        private UpdateContainerProcessStateAsyncTask(ContainerDao containerDao){
            this.containerDao=containerDao;
        }

        @Override
        protected Void doInBackground(Object... params){
            String processState=(String)params[0];
            int containerId=(int)params[1];
            containerDao.updateContainerProcessStateByContainerId(processState, containerId);
            return null;
        }
    }

    public Product getProductByDidAndContainerId(String did, int containterId){
        return productDao.getProductByDidAndContainerId(did, containterId);
    }

    public void setProductListVisibleByContainerId(boolean visible, int containerId){
        new SetProductListVisibleByContainerIdAsyncTask(productDao).execute(visible, containerId);
    }

    private static class SetProductListVisibleByContainerIdAsyncTask extends AsyncTask<Object, Void, Void>{
        private ProductDao productDao;

        private SetProductListVisibleByContainerIdAsyncTask(ProductDao productDao){
            this.productDao=productDao;
        }

        @Override
        protected Void doInBackground(Object... params){
            boolean visible=(boolean)params[0];
            int containerId=(int)params[1];
            productDao.setProductListVisibleByContainerId(visible, containerId);
            return null;
        }
    }

    public void insertDataRecord(DataRecord dataRecord){
        new InsertDataRecordAsyncTask(dataRecordDao).execute(dataRecord);
    }

    private static class InsertDataRecordAsyncTask extends AsyncTask<DataRecord, Void, Void>{
        private DataRecordDao dataRecordDao;

        private InsertDataRecordAsyncTask(DataRecordDao dataRecordDao){
            this.dataRecordDao=dataRecordDao;
        }

        @Override
        protected Void doInBackground(DataRecord... params){
            DataRecord dataRecord=params[0];
            dataRecordDao.insert(dataRecord);
            return null;
        }
    }

    public void updateDataRecord(DataRecord dataRecord){
        new UpdateDataRecordAsyncTask(dataRecordDao).execute(dataRecord);
    }

    private static class UpdateDataRecordAsyncTask extends AsyncTask<DataRecord, Void, Void>{
        private DataRecordDao dataRecordDao;

        private UpdateDataRecordAsyncTask(DataRecordDao dataRecordDao){
            this.dataRecordDao=dataRecordDao;
        }

        @Override
        protected Void doInBackground(DataRecord... params){
            DataRecord dataRecord=params[0];
            dataRecordDao.update(dataRecord);
            return null;
        }
    }

    public DataRecord getDataRecordByControllerUserId(String containerUserId){
        return dataRecordDao.getDataRecordByControllerUserId(containerUserId);
    }

    public LiveData<DataRecord> getLiveDataRecordByControllerUserId(String containerUserId){
        return dataRecordDao.getLiveDataRecordByControllerUserId(containerUserId);
    }

    public void insertEvent(Event event){
        new InsertEventAsyncTask(eventDao).execute(event);
    }

    private static class InsertEventAsyncTask extends AsyncTask<Event, Void, Void>{
        private EventDao eventDao;

        private InsertEventAsyncTask(EventDao eventDao){
            this.eventDao=eventDao;
        }

        @Override
        protected Void doInBackground(Event... params){
            Event event=params[0];
            eventDao.insert(event);
            return null;
        }
    }

    public void updateEvent(Event event){
        new UpdateEventAsyncTask(eventDao).execute(event);
    }

    private static class UpdateEventAsyncTask extends AsyncTask<Event, Void, Void>{
        private EventDao eventDao;

        private UpdateEventAsyncTask(EventDao eventDao){
            this.eventDao=eventDao;
        }

        @Override
        protected Void doInBackground(Event... params){
            Event event=params[0];
            eventDao.update(event);
            return null;
        }
    }
}
