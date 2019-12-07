package com.example.adintegrate.utils;

import com.example.adintegrate.network.Constant;

import java.util.Random;

/**
 * Created by dell on 2019/12/2 18:45
 * Description:
 * Emain: 1187278976@qq.com
 */
public class RandomSelectionUtil {

    public static int getRandomNumber(int min,int max){
        Random random = new Random();
        return random.nextInt(Constant.MAX) % (Constant.MAX - Constant.MIN + 1) + Constant.MIN;
    }


}
