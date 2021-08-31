package com.anime.movieapp.data;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmCreate extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("RealmData.realm").build();
        Realm.setDefaultConfiguration(configuration);
    }
    //multi dex;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
