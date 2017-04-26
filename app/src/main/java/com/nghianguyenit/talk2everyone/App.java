package com.nghianguyenit.talk2everyone;

import android.app.Application;

import com.nghianguyenit.talk2everyone.utils.GoogleApiHelper;

/**
 * Created by NghiaNH on 4/24/2017.
 */

public class App extends Application {
    private GoogleApiHelper googleApiHelper;
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        googleApiHelper = new GoogleApiHelper(mInstance);
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public GoogleApiHelper getGoogleApiHelperInstance() {
        return this.googleApiHelper;
    }
    public static GoogleApiHelper getGoogleApiHelper() {
        return getInstance().getGoogleApiHelperInstance();
    }
}
