package com.example.adintegrate.utils;

import android.os.Handler;
import android.util.Log;

/**
 * Created by dell on 2019/12/2 18:38
 * Description:
 * Emain: 1187278976@qq.com
 */
public class MyTimerUtil {
    private final static String TAG = "MyTimerUtil";
    private static MyTimerUtil insatnce = null;

    //计时器
    private Handler mhandle;
    private boolean isPause = false;//是否暂停
    private int currentSecond = 0;//当前毫秒数
    private Runnable timeRunable;


    private MyTimerUtil() {
        mhandle = new Handler();
        timeRunable = new Runnable() {
            @Override
            public void run() {
                currentSecond = currentSecond + 1;
                Log.i(TAG, "时间：" + currentSecond);
                if (!isPause) {
                    //递归调用本runable对象，实现每隔一秒一次执行任务
                    mhandle.postDelayed(this, 1000);
                }
            }
        };
    }

    public static MyTimerUtil getInstance() {
        if (insatnce == null)
            insatnce = new MyTimerUtil();
        return insatnce;
    }


    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }


    public int getCurrentSecond() {
        return currentSecond;
    }

    public void setCurrentSecond(int currentSecond) {
        this.currentSecond = currentSecond;
    }

    public Runnable getTimeRunable() {
        return timeRunable;
    }

    public void setTimeRunable(Runnable timeRunable) {
        this.timeRunable = timeRunable;
    }

    public Handler getMhandle() {
        return mhandle;
    }

    public void setMhandle(Handler mhandle) {
        this.mhandle = mhandle;
    }

}
