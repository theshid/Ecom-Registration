package com.ecomtrading.android.modules;

import android.content.Context;

import com.ecomtrading.android.db.MyDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn({SingletonComponent.class})
public class DatabaseModule {

    @Singleton
    @Provides
    public static MyDatabase provideDb(@ApplicationContext Context context){
        return MyDatabase.getInstance(context);
    }
}
