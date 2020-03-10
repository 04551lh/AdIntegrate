package com.example.adintegrate.utils;

import java.util.Random;

/**
 * Created by dell on 2019/12/2 18:45
 * Description:
 * Emain: 1187278976@qq.com
 */
public class RandomSelectionUtil {

    public static int getRandomNumber(int min,int max){
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }


}
