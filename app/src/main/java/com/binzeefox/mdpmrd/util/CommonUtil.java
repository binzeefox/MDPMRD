package com.binzeefox.mdpmrd.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tong.xiwen on 2017/6/13.
 */
public class CommonUtil {


    private static Toast toast;

    /**
     * Toast工具
     *
     * @param context context目标
     * @param hint    Toast信息
     */
    public static void showToast(Context context, String hint) {

        if (toast == null) {
            toast = Toast.makeText(context, hint, Toast.LENGTH_LONG);
        } else {
            toast.setText(hint);
        }
        toast.show();
    }

    /**
     * 获取字符串的MD5摘要
     *
     * @param value 待摘要字符串
     * @return 字符串MD5码
     */
    public static String md5(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(value.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = 0 + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 判断一个字符是否是中文
    private static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    public static boolean isChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c))
                return true;// 有一个中文字符就返回
        }
        return false;
    }

}
