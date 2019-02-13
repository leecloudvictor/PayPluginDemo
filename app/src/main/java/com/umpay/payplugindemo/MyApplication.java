package com.umpay.payplugindemo;

import android.app.Application;

import com.umpay.payplugin.UMPay;

/**
 * Created by tianxiaoyang on 2017/4/21.
 */
public class MyApplication extends Application {
    public static MyApplication instance;
    @Override
    public void onCreate() {
        instance=this;
        super.onCreate();
        UMPay.getInstance().debug(true);
        UMPay.getInstance().init(this);
    }
}
