package com.test.feulmgmt;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.stripe.android.PaymentConfiguration;

public class FuelApplication extends Application {

    private static Application context;
    private IntentFilter intentFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_TYooMQauvdEDq54NiTphI7jx"
        );
    }

    public static Context getContext() {
        return context;
    }

}
