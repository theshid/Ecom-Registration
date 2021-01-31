package com.ecomtrading.android.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ecomtrading.android.converters.DateConverter;
import com.ecomtrading.android.entity.CommunityInformation;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;


@Database(entities = CommunityInformation.class, version = 3, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MyDatabase extends RoomDatabase {
    public abstract Dao dao();

    public static MyDatabase sInstance = null;
    private static final String DB_NAME = "database";
    private static final Object LOCK = new Object();


    public static MyDatabase getInstance( Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, MyDatabase.DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build();

            }

        }
        return sInstance;
    }

}
