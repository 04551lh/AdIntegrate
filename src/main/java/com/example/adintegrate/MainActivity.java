package com.example.adintegrate;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.example.adintegrate.bean.RequestBean;
import com.example.adintegrate.bean.SucceedBean;
import com.example.adintegrate.bean.TaskBean;
import com.example.adintegrate.network.Constant;
import com.example.adintegrate.network.OkHttpHelper;
import com.example.adintegrate.utils.MyTimerUtil;
import com.example.adintegrate.utils.ObtainUtil;
import com.example.adintegrate.utils.RandomSelectionUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";
    //请求数据
    private RequestBean mRequestBean;
    //接收网络数据
    private TaskBean mTaskBean;
    //上传网络数据
    private SucceedBean mSucceedBean;
    private SucceedBean.DataBean mDataBean;
    //网络请求类
    private OkHttpHelper mOkHttpHelper;
    //webview加载的类型
    private String mLoadType;
    //执行的次数
    private int mExecTimes;
    //初始化次数
    private int mInitExecTimes;
    //时间控件
    private Timer mTimer;
    //停留的时间
    private int mStayTime;
    //计时器
    private MyTimerUtil mMyTimerUtil;
    //请求所用时间
    private int totalTime;
    //webview 加载处理
    private WebViewClient mWebViewClient;
    //加载的Webview
    private WebView mWebView;
    //User-Agent和Referer
    private Map<String, String> mHeaderParams;
    private Map<String, String> mLoadResourceParams;
    //替换参数
    private Map<String, String> mReplaceDirectParams;
    private Map<String, String> mReplaceIndirectParams;
    //是否关闭
    private boolean mClose;
    //统计流量
    private long mStartByte,mEndByte;
    //webview 宽高
    private int mH,mW;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (mInitExecTimes < mExecTimes) {
//                    mStartByte = 0;
                    mStartByte = TrafficStats.getUidRxBytes(ObtainUtil.getUid(MainActivity.this)) +
                            TrafficStats.getUidTxBytes(ObtainUtil.getUid(MainActivity.this));

                    if(mLoadType.equals(Constant.URL_JUMP))
                        mWebView.loadUrl(mDataBean.getUrl(), mHeaderParams);
                    else mWebView.loadDataWithBaseURL(null,mDataBean.getUrl(), "application/json",  "utf-8", null);
//                    mWebView.loadUrl(mDataBean.getUrl(), mHeaderParams);
                } else {
                    mTimer.cancel();
                    if (mClose) {
                        finish();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = new WebView(this);
        if (mWebView == null) {

        }
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        //NORMAL：正常显示，没有渲染变化。
        //SINGLE_COLUMN：把所有内容放到WebView组件等宽的一列中。
        //NARROW_COLUMNS：可能的话，使所有列的宽度不超过屏幕宽度。
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        initData();
        firstRequest();

        mWebView.setLayoutParams(new ViewGroup.LayoutParams(mW,mH));
        if(mLoadType.equals(Constant.URL_JUMP))
        mWebView.loadUrl(mDataBean.getUrl(), mHeaderParams);
        else mWebView.loadDataWithBaseURL(null,mDataBean.getUrl(), "application/json",  "utf-8", null);
        mWebView.setWebViewClient(mWebViewClient);
        setContentView(mWebView);
    }

    private void initData() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mClose = false;
        mInitExecTimes = 0;
        mW = 0;
        mH = 0;
        mHeaderParams = new HashMap<>();
        mLoadResourceParams = new HashMap<>();
        mTimer = new Timer();
        mSucceedBean = new SucceedBean();
        mDataBean = new SucceedBean.DataBean();
        mDataBean.setCid(ObtainUtil.getIMEI(this));
        mDataBean.setIp(ObtainUtil.getIPAddress(this));
        mRequestBean = new RequestBean();
        mRequestBean.setCid(ObtainUtil.getIMEI(this));
        mRequestBean.setIp(ObtainUtil.getIPAddress(this));
//        Log.i(TAG,"getPhoneInfo："+ ObtainUtil.getPhoneInfo(this));
//        mSucceedBean.setSim(ObtainUtil.getPhoneInfo(this));
        mOkHttpHelper = OkHttpHelper.getInstance();
        mMyTimerUtil = MyTimerUtil.getInstance();
        mWebViewClient = new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i(TAG,"onPageStarted："+url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.i(TAG, "onLoadResource：" + url);
                super.onLoadResource(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().contains("http") || request.getUrl().toString().contains("https")) {
                    mWebView.loadUrl(request.getUrl().toString(), mLoadResourceParams);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mMyTimerUtil.setPause(true);
                totalTime = mMyTimerUtil.getCurrentSecond();
                Log.i(TAG, "totalTime：" + totalTime);
                Log.i(TAG, "mStayTime：" + mStayTime);
                mInitExecTimes++;
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {                // (1) 使用handler发送消息
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                }, mStayTime * 1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
                if(mInitExecTimes == mExecTimes) mClose = true;
                mEndByte = TrafficStats.getUidRxBytes(ObtainUtil.getUid(MainActivity.this)) +
                        TrafficStats.getUidTxBytes(ObtainUtil.getUid(MainActivity.this));
                Log.i(TAG,mEndByte+"");
                Log.i(TAG,ObtainUtil.FormetFileSize(mEndByte)+"");
                Log.i(TAG, "total：" + ObtainUtil.FormetFileSize(TrafficStats.getUidRxBytes(ObtainUtil.getUid(MainActivity.this)) +
                        TrafficStats.getUidTxBytes(ObtainUtil.getUid(MainActivity.this))));
                Log.i(TAG,"traffic："+ (mEndByte- mStartByte));
                mSucceedBean.setFlow(Integer.parseInt((mEndByte- mStartByte)+""));
                mDataBean.setIs_succeed(true);
                mDataBean.setExec_time((mStayTime+totalTime));
                mSucceedBean.setData(mDataBean);
                succeed(mSucceedBean);
                super.onPageFinished(view, url);
            }
        };
    }

    private void firstRequest() {
        mMyTimerUtil.setCurrentSecond(0);
        mMyTimerUtil.getMhandle().postDelayed(mMyTimerUtil.getTimeRunable(), 0);
        String json = new Gson().toJson(mRequestBean);
        mStartByte = 0;
        mStartByte = TrafficStats.getUidRxBytes(ObtainUtil.getUid(this)) +
                TrafficStats.getUidTxBytes(ObtainUtil.getUid(this));

        Log.i(TAG,mStartByte+"");
        Log.i(TAG,ObtainUtil.FormetFileSize(mStartByte)+"");
        Log.i(TAG, "total：" + ObtainUtil.FormetFileSize(TrafficStats.getUidRxBytes(ObtainUtil.getUid(this)) +
                TrafficStats.getUidTxBytes(ObtainUtil.getUid(this))));
        String response = mOkHttpHelper.post(Constant.TASK_URL, json);
//        response = response.replace("\\", "");
//        response = response.substring(1, response.length() - 1);//此处也可以解析为byte[],Reader,InputStream
        mTaskBean = new Gson().fromJson(response, TaskBean.class);
        TaskBean.DataBean.MessageBean messageBean = mTaskBean.getData().getMessage();
        mLoadType = messageBean.getExec_type();

        if(messageBean.getResolution() != null){
            mW = Integer.parseInt(messageBean.getResolution().getW() + "");
            mH = Integer.parseInt(messageBean.getResolution().getH() + "");
        }

        if (!TextUtils.isEmpty(messageBean.getUa())) {
            mHeaderParams.put(Constant.USER_AGNRT, messageBean.getUa());
            mLoadResourceParams.put(Constant.REFERER, messageBean.getUa());
        }
        int refererSize = messageBean.getReferer() == null?0:messageBean.getReferer().getValues().size();
        Log.i(TAG,"refererSize："+refererSize);
        if (refererSize > 0)replaceHeader(messageBean.getReferer(),refererSize);
        int replaceSize = messageBean.getReplace() == null?0:messageBean.getReplace().size();
        if(replaceSize > 0)replaceParems(messageBean.getReplace());
        mLoadType = messageBean.getExec_type();
        mExecTimes = Integer.parseInt(messageBean.getExec_times() + "");
        mStayTime = Integer.parseInt(messageBean.getStay_time() + "");
        mDataBean.setTid(Integer.parseInt(messageBean.getTid() + ""));
        mDataBean.setUrl(messageBean.getExec_code() + "");
    }

    private void succeed(SucceedBean succeedBean) {
        String json = new Gson().toJson(succeedBean);
        String response = mOkHttpHelper.post(Constant.REPORT_URL, json);
        Log.i(TAG, response);
    }


    private void replaceHeader(TaskBean.DataBean.MessageBean.RefererBean refererBean,int refererSize){
        //todo 随机生成
        int randomNumber = RandomSelectionUtil.getRandomNumber(0,refererSize);
        Log.i(TAG,"randomNumber："+randomNumber);
        String referer = refererBean.getValues().get(randomNumber);
        String target = refererBean.getTarget();
        switch (target){
            case Constant.ALL:
                mHeaderParams.put(Constant.REFERER,referer);
                mLoadResourceParams.put(Constant.REFERER,referer);
                break;
            case Constant.DIRECT:
                mHeaderParams.put(Constant.REFERER,referer);
                break;
            case Constant.INDIRECT:
                mLoadResourceParams.put(Constant.REFERER,referer);
                break;
        }
    }

    private void replaceParems(List<TaskBean.DataBean.MessageBean.ReplaceBean> replaceBeanList){
        //todo 随机生成
        for (TaskBean.DataBean.MessageBean.ReplaceBean replaceBean:
                replaceBeanList) {
            String tag = replaceBean.getTag();
            String value = replaceBean.getValue();
            String target = replaceBean.getTarget();
            switch (target){
                case Constant.ALL:
                    mReplaceDirectParams.put(tag,value);
                    mReplaceIndirectParams.put(tag,value);
                    break;
                case Constant.DIRECT:
                    mReplaceDirectParams.put(tag,value);
                    break;
                case Constant.INDIRECT:
                    mReplaceIndirectParams.put(tag,value);
                    break;
            }
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            //按返回键操作并且能回退网页
//            if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
//                //后退
//                mWebView.goBack();
//                return true;
//            }
            if (keyCode == KeyEvent.KEYCODE_BACK ) {
                //后退
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
