package com.ecomtrading.android.modules;

import android.content.Context;

import com.ecomtrading.android.utils.Session;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class SharedPreferenceModule {

    @Provides
    @Singleton
    public static Session provideSessionManager(@ActivityContext Context context){
        return new Session(context);
    }

}
