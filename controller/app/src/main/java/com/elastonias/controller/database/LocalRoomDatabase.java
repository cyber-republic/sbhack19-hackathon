package com.elastonias.controller.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.elastonias.controller.database.dao.ContainerDao;
import com.elastonias.controller.database.dao.DataRecordDao;
import com.elastonias.controller.database.dao.EventDao;
import com.elastonias.controller.database.dao.ProductDao;
import com.elastonias.controller.database.entity.Container;
import com.elastonias.controller.database.entity.DataRecord;
import com.elastonias.controller.database.entity.Event;
import com.elastonias.controller.database.entity.Product;


@Database(entities={Container.class, Product.class, DataRecord.class, Event.class}, version=1)
public abstract class LocalRoomDatabase extends RoomDatabase{
    public abstract ContainerDao containerDao();
    public abstract ProductDao productDao();
    public abstract DataRecordDao dataRecordDao();
    public abstract EventDao eventDao();

    private static volatile LocalRoomDatabase INSTANCE;

    public static LocalRoomDatabase getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (LocalRoomDatabase.class){
                if(INSTANCE==null){
                    INSTANCE=Room.databaseBuilder(context.getApplicationContext(), LocalRoomDatabase.class, "local_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback=new RoomDatabase.Callback(){
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            //new InitProductDIDsAsyncTask(INSTANCE).execute();
            new SetContainersOfflineAsyncTask(INSTANCE).execute();
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
        }
    };

    private static class InitProductDIDsAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ProductDao productDao;

        InitProductDIDsAsyncTask(LocalRoomDatabase localDatabase){
            productDao=localDatabase.productDao();
        }

        @Override
        protected Void doInBackground(final Void... params){
            Product product1=new Product(0, "ifDgNyKaGNYAJ4BdDGouCnX344RN9uvBFz", "8599812C374B63E95512F617E7B11C94A0469015711FD40746B5ACA8248B3ED9", "02599250A16E2D45DDCCDF1920A278FF09E82A17728EAB45705E6D1841496CCD84", -1, false, false);
            Product product2=new Product(0, "iZpLrUxermVrsrxcbZKrKbnMzSvzZop95y", "6F65EE4BC07E3EE80043C79F2520A60A8961181A4BF6D2005CEBABFBAFBDE61C", "030120298C4772FC37FF913A58FA870E14A196E703581AE679D7E14ACADFD520B0", -1, false, false);
            Product product3=new Product(0, "ijjRSWepNZXnZVk9s5mB7HpvA62FW1BQhC", "91F58A48B48750295D34D5DBAF2B38EBB4E948A67E1086D15AAB7EF03D44C43C", "0395E2470AF4CCE2D02F9DEF920B7D45417441B7DDE966B2EF5367D69AC16C403E", -1, false, false);
            productDao.insert(product1);
            productDao.insert(product2);
            productDao.insert(product3);

            return null;
        }
    }

    private static class SetContainersOfflineAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ContainerDao containerDao;

        SetContainersOfflineAsyncTask(LocalRoomDatabase localDatabase){
            containerDao=localDatabase.containerDao();
        }

        @Override
        protected Void doInBackground(final Void... params){
            containerDao.setContainerListConnectionState("offline");

            return null;
        }
    }




}
