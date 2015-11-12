package com.grabtaxi.themoviedb;

import android.app.Application;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Database.getInstance().open(getApplicationContext());
        MyVolley.init(getApplicationContext());
    }
}
