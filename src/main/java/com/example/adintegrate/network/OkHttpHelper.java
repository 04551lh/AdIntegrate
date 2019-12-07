package com.example.adintegrate.network;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by dell on 2019/12/2 17:43
 * Description:
 * Emain: 1187278976@qq.com
 */
public class OkHttpHelper {
    private final static String TAG = "OkHttpHelper";
    //网络请求日志打印
    private HttpLoggingInterceptor mLogging;
    //网络请求
    private OkHttpClient mOkHttpClient;

    private final static int CONNECT_TIMEOUT = 3;
    private final static int READ_TIMEOUT = 3;
    private final static int WRITE_TIMEOUT = 3;
    private static OkHttpHelper instance = null;

    public HashMap<String, List<Cookie>> getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(HashMap<String, List<Cookie>> cookieStore) {
        this.cookieStore = cookieStore;
    }

    private HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
    private OkHttpHelper() {
        mLogging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, message);
            }
        });
        mLogging.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(mLogging)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
    }

    public static OkHttpHelper getInstance() {
        if (instance == null) {
            instance = new OkHttpHelper();
        }
        return instance;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String post(String url, String json) {
        RequestBody body = RequestBody.create(Constant.JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = mOkHttpClient.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
