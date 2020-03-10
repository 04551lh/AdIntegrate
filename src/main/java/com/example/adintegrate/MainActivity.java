package com.example.adintegrate;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.adintegrate.bean.RequestBean;
import com.example.adintegrate.bean.RespomseSuccessBean;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements MyException {

    private final static String TAG = "MainActivity";
    //请求数据
    private RequestBean mRequestBean;
    //上传网络数据
    private SucceedBean mSucceedBean;
    private SucceedBean.DataBean mDataBean;
    //网络请求类
    private OkHttpHelper mOkHttpHelper;
    //初始化次数
    private int mInitExecTimes;
    //时间控件
    private Timer mTimer;
    //计时器
    private MyTimerUtil mMyTimerUtil;
    //请求所用时间
    private int totalTime;
    //webview 加载处理
    private WebViewClient mWebViewClient;
    //加载的Webview
    private WebView mWebView;
    private Map<String, String> mRefererParams;
    //webview 宽高
    private int mH, mW;
    //替换参数
    private List<TaskBean.DataBean.MessageBean.ReplaceBean> mReplaceUrlList;
    //优化整理
    private TaskBean.DataBean.MessageBean mTaskBean;
    //执行的URL
    private String execUrl;
    //替换的referer
    private String mRefererValue;
    private String mRefererTarget;
    //是否关闭
    private boolean mClose;
    //统计流量
    private long mStartByte, mEndByte;
    private long mRxBytesStart, mTxBytesStart;
    private long mRxBytesEnd, mTxBytesEnd;
    //uid
    private int mUid;
    private CookieManager cookieManager;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (mClose) {
                    finish();
                    return;
                }
                mMyTimerUtil.setCurrentSecond(0);
                Log.i("YZG", "execTimes->>>>>>>>>mInitExecTimes" + mInitExecTimes);
                execURL(execUrl);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = new WebView(this);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        mWebView.loadUrl("http://active.bjddcy.com/rtc/rand_req_test.php?imei=__IMEI__&os=__OS__&ip=__IP__&idfa=__IDFA__&date=0114");
//        setContentView(mWebView);
        initData();
        firstRequest();
        try {
            if (!"".equals(mTaskBean.getUa())) webSettings.setUserAgentString(mTaskBean.getUa());
            initWebViewClient();
            mWebView.setWebViewClient(mWebViewClient);

            myLoadUrl(mReplaceUrlList, mTaskBean.getExec_code().get(0));
            TaskBean.DataBean.MessageBean.ResolutionBean resolutionBean = mTaskBean.getResolution();
            if (!"".equals(resolutionBean.getW()) || "".equals(resolutionBean.getH())) {
                Log.i(TAG, "resolutionBean");
                mW = Integer.parseInt(resolutionBean.getW() + "");
                mH = Integer.parseInt(resolutionBean.getH() + "");
                mWebView.setLayoutParams(new ViewGroup.LayoutParams(mW, mH));
                setContentView(mWebView, new ViewGroup.LayoutParams(mW, mH));
            } else {
                Log.i(TAG, "resolutionBean false");
                setContentView(mWebView);
            }
        } catch (NullPointerException e) {
            finish();
        }
    }

    private void myLoadUrl(List<TaskBean.DataBean.MessageBean.ReplaceBean> mReplaceUrlList, String exec_code) {
        execUrl = myReplaceExecUrl(mReplaceUrlList, exec_code);
        execURL(execUrl);
    }

    private void execURL(String url) {
//        mWebView.loadUrl("about:blank");
        if (Constant.URL_JUMP.equals(mTaskBean.getExec_type())) {
            mRefererParams.put(Constant.REFERER, mRefererValue);
            mWebView.loadUrl(url, mRefererParams);
        } else {
            mWebView.loadData(url, "application/json", "UTF-8");
        }
    }

    private void initWebViewClient() {
        mWebViewClient = new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().trim();
                if (!TextUtils.isEmpty(url)) {
                    Log.i(TAG, "WebResourceResponse start url " + url);
                    if (execUrl.equals(url)) {
                        Log.i(TAG, "WebResourceResponse:first");
                        request.getRequestHeaders().put(Constant.REFERER, mRefererValue);
                        return super.shouldInterceptRequest(view, request);
                    }else if(mTaskBean.getIgnore_list().size() > 0){
                        for (String str: mTaskBean.getIgnore_list()) {
                            if(str.equals(url)){
                                request.getRequestHeaders().put(Constant.REFERER, mRefererValue);
                                return super.shouldInterceptRequest(view, request);
                            }
                        }
                    } else {
                        boolean isReplace = false;
                        String os = "";
                        String imei = "";
                        String idfa = "";
                        for (TaskBean.DataBean.MessageBean.ReplaceBean replaceUrlBean : mReplaceUrlList) {
                            String target = replaceUrlBean.getTarget();
                            String tag = replaceUrlBean.getTag();
                            String value = replaceUrlBean.getValue();
                            if (target.equals(Constant.INDIRECT) || target.equals(Constant.ALL)) {
                                if (Constant.OS.equals(tag)) {
                                    url = url.replace(tag, value);
                                    os = value;
                                } else if (Constant.IMEI.equals(tag)) {
                                    imei = value;
                                } else if (Constant.IDFA.equals(tag)) {
                                    idfa = value;
                                } else {
                                    url = url.replace(tag, value);
                                }
                            }
                            if (url.contains(tag)) {
                                isReplace = true;
                            }
                        }
                        if ("0".equals(os)) {
                            url = url.replace(Constant.IMEI, imei);
                        } else if ("1".equals(os)) {
                            url = url.replace(Constant.IDFA, idfa);
                        }

                        if (isReplace) {
                            Log.i(TAG, "WebResourceResponse end url " + url);
                            StringBuilder stringBuilder = myLoadResource(mRefererTarget, url);
                            return new WebResourceResponse("", "", new ByteArrayInputStream(stringBuilder.toString().getBytes()));
                        } else {
                            if (mRefererTarget.equals(Constant.DIRECT) || mRefererTarget.equals(Constant.ALL)) {
                                request.getRequestHeaders().put(Constant.REFERER, mRefererValue);
                                return super.shouldInterceptRequest(view, request);
                            }
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.i("YZG", "onPageStarted访问的URL------------------------------->" + url);
                Log.i("YZG", "onPageStarted次数------------------------------->" + mInitExecTimes);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("yzg", "onPageFinished------------------>" + url);
                String CookieStr = cookieManager.getCookie(url);
                Log.i(TAG, "CookieStr：" + CookieStr);
                mRxBytesEnd = TrafficStats.getUidRxBytes(mUid);
                mTxBytesEnd = TrafficStats.getUidTxBytes(mUid);
                mEndByte = mRxBytesEnd + mTxBytesEnd;
                mMyTimerUtil.setPause(true);
                totalTime = mMyTimerUtil.getCurrentSecond();
                mSucceedBean.setFlow(Integer.parseInt((mEndByte - mStartByte) + ""));
                mStartByte = mEndByte;
                mDataBean.setIs_succeed(true);
                int stayTime = RandomSelectionUtil.getRandomNumber(Integer.parseInt(mTaskBean.getStay_time().getMin() + ""), Integer.parseInt(mTaskBean.getStay_time().getMax() + ""));
                mDataBean.setExec_time((stayTime + totalTime));
                mDataBean.setUrl(mTaskBean.getExec_code().get(0));
                mDataBean.setTid(mTaskBean.getTid());
                mSucceedBean.setData(mDataBean);
                int execTimes = Integer.parseInt(mTaskBean.getExec_times());
                mInitExecTimes++;
                if (mInitExecTimes == execTimes) {
                    Log.i(TAG, "execTimes->>>>>>>>>finish" + execTimes);
                    CookieManager.getInstance().removeAllCookie();
                    CookieSyncManager.createInstance(MainActivity.this);
                    mClose = true;
                }
                succeed(mSucceedBean);
                if (mTimer == null) {
                    mTimer = new Timer();
                }
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {                // (1) 使用handler发送消息
                        Message message = new Message();
                        message.what = 0;
                        mHandler.sendMessage(message);
                    }
                }, stayTime * 1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
//                super.onPageFinished(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                mWebView.loadUrl(request.getUrl().toString());
                return true;
            }
        };
    }

    private void initData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mReplaceUrlList = new ArrayList<>();
        mRefererParams = new HashMap<>();
        cookieManager = CookieManager.getInstance();
        mClose = false;
        mInitExecTimes = 0;
        mTimer = new Timer();
        mSucceedBean = new SucceedBean();
        mDataBean = new SucceedBean.DataBean();

        String mCid = ObtainUtil.getIMEI(this) == null ? "" : ObtainUtil.getIMEI(this);
        String mIP = ObtainUtil.getIPAddress(this) == null ? "" : ObtainUtil.getIPAddress(this);

        mDataBean.setCid(mCid);
        mDataBean.setIp(mIP);

        mRequestBean = new RequestBean();
        mRequestBean.setCid(mCid);
        mRequestBean.setIp(mIP);

        mOkHttpHelper = OkHttpHelper.getInstance();
        mOkHttpHelper.setMyException(this);
        mMyTimerUtil = MyTimerUtil.getInstance();
    }

    private void firstRequest() {
        mMyTimerUtil.setCurrentSecond(0);
        mMyTimerUtil.getMhandle().postDelayed(mMyTimerUtil.getTimeRunable(), 0);
        String json = new Gson().toJson(mRequestBean);
        mUid = ObtainUtil.getUid(this);
        mRxBytesStart = TrafficStats.getUidRxBytes(mUid);
        mTxBytesStart = TrafficStats.getUidTxBytes(mUid);
        mStartByte = mRxBytesStart + mTxBytesStart;
        String response;
        try {
            response = mOkHttpHelper.post(Constant.TASK_URL, json);
            Log.i(TAG, "response：" + response);
            if (!response.contains("200")) {
                mMyTimerUtil.setPause(true);
                finish();
                return;
            }
            TaskBean taskBean = new Gson().fromJson(response, TaskBean.class);
            mTaskBean = taskBean.getData().getMessage();
            //接收网络数据
            initNetwork(mTaskBean);
        } catch (NullPointerException e) {
//            Toast.makeText(MainActivity.this, "服务器异常~", Toast.LENGTH_SHORT).show();
            mMyTimerUtil.setPause(true);
            finish();
        }
    }

    private void succeed(SucceedBean succeedBean) {
        String json = new Gson().toJson(succeedBean);
        String response = null;
        try {
            response = mOkHttpHelper.post(Constant.REPORT_URL, json);
            if (!response.equals("200")) {

            } else {
                finish();
            }
        } catch (NullPointerException e) {
            finish();
        }
        RespomseSuccessBean respomseSuccessBean = new Gson().fromJson(response, RespomseSuccessBean.class);
        if (1 == respomseSuccessBean.getTaskstatus()) {
            finish();
        }
    }

    private void initNetwork(TaskBean.DataBean.MessageBean messageBean) {
        List<String> refererValueList = messageBean.getReferer().getValues();
        int refererValueListSize = refererValueList.size();
        if (refererValueListSize != 0) {
            int randomNumber = RandomSelectionUtil.getRandomNumber(0, refererValueListSize);
            mRefererValue = refererValueList.get(randomNumber);
            mRefererTarget = messageBean.getReferer().getTarget();
        }
        mReplaceUrlList.addAll(messageBean.getReplace());
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
                httpURLConnection.setRequestProperty(Constant.REFERER, mRefererValue);
            } else {
                httpURLConnection.setRequestProperty(Constant.REFERER, mDataBean.getUrl());
            }
            if (!("").equals(mTaskBean.getUa())) {
                httpURLConnection.setRequestProperty(Constant.USER_AGENT, mTaskBean.getUa());
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

    private String myReplaceExecUrl(List<TaskBean.DataBean.MessageBean.ReplaceBean> list, String url) {
        String realUrl = url;
        String os = "";
        String imei = "";
        String idfa = "";
        for (TaskBean.DataBean.MessageBean.ReplaceBean replaceUrlBean : list) {
            String target = replaceUrlBean.getTarget();
            String tag = replaceUrlBean.getTag();
            String value = replaceUrlBean.getValue();
            if (Constant.DIRECT.equals(target) || target.equals(Constant.ALL)) {
                if (Constant.OS.equals(tag)) {
                    realUrl = realUrl.replace(tag, value);
                    os = value;
                } else if (Constant.IMEI.equals(tag)) {
                    imei = value;
                } else if (Constant.IDFA.equals(tag)) {
                    idfa = value;
                } else {
                    realUrl = realUrl.replace(tag, value);
                }
            }
        }
        if ("0".equals(os)) {
            realUrl = realUrl.replace(Constant.IMEI, imei);
        } else if ("1".equals(os)) {
            realUrl = realUrl.replace(Constant.IDFA, idfa);
        }

        Log.i("realUrl", "realUrl" + realUrl);
        return realUrl;
    }

//    private WebResourceResponse getNewResponse(String url, Map<String, String> headers) {
//        try {
//            OkHttpClient httpClient = new OkHttpClient();
//            Request.Builder builder = new Request.Builder().url(url.trim()).addHeader(Constant.REFERER, mReferer);
////            Request.Builder builder = new Request.Builder().url(url.trim());
//            Set<String> keySet = headers.keySet();
//            for (String key : keySet) {
//                builder.addHeader(key, headers.get(key));
//            }
//            Request request = builder.build();
//            final Response response = httpClient.newCall(request).execute();
//            String conentType = response.header("Content-Type", response.body().contentType().type());
//            String temp = conentType.toLowerCase();
//            if (temp.contains("charset=utf-8")) {
//                conentType = conentType.replaceAll("(?i)" + "charset=utf-8", "");
//                //不区分大小写的替换
//            }
//            if (conentType.contains(";")) {
//                conentType = conentType.replaceAll(";", "");
//                conentType = conentType.trim();
//            }
//            InputStream data = response.body().byteStream();
//            Log.i(TAG, "index:" + data.toString().length());
//            return new WebResourceResponse(conentType, response.header("Content-Encoding", "utf-8"), data);
//        } catch (Exception e) {
//            return null;
//        }
//    }

    @Override
    public void show(String str) {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void RecordLog(String sb) {
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        try {
            String fileName = String.format("log-%s.log", df.format(new Date(System.currentTimeMillis())));
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                @SuppressLint("SdCardPath") String path = "/sdcard/multiThread/log/";
                File dir = new File(path);
                if (!dir.exists()) dir.mkdirs();
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.getBytes());
                fos.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.loadUrl("about:blank");
    }
}
