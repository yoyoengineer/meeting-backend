package com.meeting.binary.android.binarymeeting.realm_init;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by meldi on 4/6/2018.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration configuration = new RealmConfiguration.Builder()
                //.modules(Realm.getDefaultModule())//default configuration that includes the models that extends realm in our database
                .name("binarybase.realm")
                .build();

        Realm.setDefaultConfiguration(configuration);

    }


}
