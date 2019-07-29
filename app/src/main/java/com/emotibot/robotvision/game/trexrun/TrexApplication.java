package com.emotibot.robotvision.game.trexrun;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class TrexApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
