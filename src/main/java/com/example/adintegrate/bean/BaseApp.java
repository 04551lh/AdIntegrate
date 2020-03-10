package com.example.adintegrate.bean;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by dell on 2019/12/16 12:31
 * Description:
 * Emain: 1187278976@qq.com
 */
public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "2d31453a6a", true);
    }
}
