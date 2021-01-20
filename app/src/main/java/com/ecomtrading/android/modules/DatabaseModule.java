package com.ecomtrading.android.modules;

import android.content.Context;

import com.ecomtrading.android.db.MyDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Singleton
    @Provides
    public MyDatabase provideDb(@ApplicationContext Context context){
        return MyDatabase.getInstance(context);
    }
}
