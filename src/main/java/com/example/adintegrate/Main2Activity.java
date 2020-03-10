package com.example.adintegrate;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.adintegrate.utils.RandomSelectionUtil;

public class Main2Activity extends Activity {

    private static String TAG = "Main2Activity";
    private TextView mTvCount;
    private int mSum = 1;
    private int mNumber;
    private int mTimer = 0;
    final Handler mHandler = new Handler();
    private boolean isFirst = false;
    private Runnable mRunnable;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            isFirst = true;
            return;
        }
        mNumber = RandomSelectionUtil.getRandomNumber(300, 900);
        Log.i(TAG, "随机数：" + mNumber);
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mTimer = mTimer + 1;
                if(mTimer > 900){
                    mTimer =0;
                }
                Log.i(TAG, "时间：" + mTimer);
                if (mTimer == mNumber) {
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                    mTimer = 0;
                    return;
                }
                //递归调用本runable对象，实现每隔一秒一次执行任务
                mHandler.postDelayed(this, 1000);
            }
        };
        mRunnable.run();
        mSum++;
        mTvCount.setText("次数：" + mSum);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mTvCount = findViewById(R.id.count);
        mTvCount.setText("次数：" + mSum);
        startActivity(new Intent(Main2Activity.this, MainActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer = mNumber;
    }
}
