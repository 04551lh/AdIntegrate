package com.example.adintegrate;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.adintegrate.utils.RandomSelectionUtil;

public class Main2Activity extends Activity {

    private TextView count;
    private int sum = 1;

    private int number;
    private int timer = 0;
    final Handler mhandle = new Handler();
    private boolean isFirst = false;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("isFirst", isFirst + "");
        if (!isFirst) {
            isFirst = true;
            return;
        }
        number = RandomSelectionUtil.getRandomNumber(300, 900);
        Log.i("Main2Activity", "随机数：" + number);
        new Runnable() {
            @Override
            public void run() {
                timer = timer + 1;
                Log.i("Main2Activity", "时间：" + timer);
                if (timer == number) {
                    startActivity(new Intent(Main2Activity.this, MainActivity.class));
                    timer = 0;
                    return;
                }
                //递归调用本runable对象，实现每隔一秒一次执行任务
                mhandle.postDelayed(this, 1000);
            }
        }.run();
        sum++;
        count.setText("次数：" + sum);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        count = findViewById(R.id.count);
        count.setText("次数：" + sum);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        String packageName = "com.example.wifi"; //另一个app的包名
//        String className = "com.example.wifi.service.MyService"; //另一个app要启动的组件的全路径名
//        intent.setClassName(packageName, className);startService(intent);//或者bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 都能启动

        startActivity(new Intent(Main2Activity.this, MainActivity.class));
    }
}
