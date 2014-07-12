package net.itsuha.flickr_twicca.utils;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * Created by dmp on 14/07/06.
 */
public class MyApp extends Application {
    private static final String LOGTAG = MyApp.class.getSimpleName();
    private static MyApp sInstance;

    public static MyApp getInstance(){
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialization
        sInstance = this;
    }

    public static void toast(String message){
        Toast.makeText(sInstance, message, Toast.LENGTH_LONG ).show();
    }

    public static void toast(@StringRes int resId){
        Toast.makeText(sInstance, resId, Toast.LENGTH_LONG).show();
    }
}
