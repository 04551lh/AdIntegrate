package com.example.adintegrate.network;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.example.adintegrate.utils.MyException;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

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
    //网络请求
    private OkHttpClient mOkHttpClient;

    private final static int CONNECT_TIMEOUT = 3;
    private final static int READ_TIMEOUT = 3;
    private final static int WRITE_TIMEOUT = 3;
    private static OkHttpHelper instance = null;

    public void setMyException(MyException myException) {
        this.myException = myException;
    }

    private MyException myException;


    private OkHttpHelper() {
        //网络请求日志打印
        HttpLoggingInterceptor mLogging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NotNull String message) {
                Log.i(TAG, message);
            }
        });
        mLogging.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(mLogging)
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

//        Request request = addHeader(url,body);
        try (Response response = mOkHttpClient.newCall(request).execute()) {
            if(response.body() == null){
                return "";
            }else{
                return response.body().string();
            }
        }
        catch (JsonSyntaxException e) {
            myException.show("服务器异常，请重新再试~"+e.toString());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            myException.show("服务器异常，请重新再试~"+e.toString());
            e.printStackTrace();
        }catch (SocketTimeoutException e) {
            myException.show("服务器异常，请重新再试~"+e.toString());
            e.printStackTrace();
        } catch (UnknownHostException e) {
            myException.show("服务器异常，请重新再试~"+e.toString());
            e.printStackTrace();
        } catch (ConnectException e) {
            myException.show("服务器异常，请重新再试~"+e.toString());
            e.printStackTrace();
        }

        catch (IOException e) {
            myException.show("服务器异常，请重新再试~"+e.toString());
            e.printStackTrace();
        }
        return null;
    }

}
