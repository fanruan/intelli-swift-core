package com.fr.swift.cloud.groovy;

import com.fr.swift.cloud.log.SwiftLoggers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/9/1
 */
public class EncryptUtil {

    /**
     * 传入文本内容，返回 encrypt-256 串
     */
    public static String SHA256(final String strText) {
        return encrypt(strText, "SHA-256");
    }

    /**
     * 传入文本内容，返回 encrypt-512 串
     */
    public static String SHA512(final String strText) {
        return encrypt(strText, "SHA-512");
    }

    public static String MD5(final String strText) {
        return encrypt(strText, "MD5");
    }

    /**
     * 字符串 encrypt 加密
     */
    private static String encrypt(final String str, final String strType) {
        MessageDigest messageDigest;
        String encodeStr = "";
        if (null == str || str.length() == 0) {
            return encodeStr;
        }
        try {
            messageDigest = MessageDigest.getInstance(strType);
            messageDigest.update(str.getBytes());

            // 将byte 转换为字符展示出来
            StringBuilder stringBuffer = new StringBuilder();
            String temp;
            for (byte aByte : messageDigest.digest()) {
                temp = Integer.toHexString(aByte & 0xFF);
                if (temp.length() == 1) {
                    //1得到一位的进行补0操作
                    stringBuffer.append("0");
                }
                stringBuffer.append(temp);
            }
            encodeStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            SwiftLoggers.getLogger().error(e);
        }
        return encodeStr;
    }
}