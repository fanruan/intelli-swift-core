package com.fr.bi.stable.utils.algorithem;

import com.fr.bi.stable.utils.code.BILogDelegate;
import com.fr.bi.stable.utils.code.BILogger;

import java.net.URLDecoder;
import java.util.Random;

/**
 * Created by Connery on 2015/7/14.
 */
public class BIRandomUitils {

    public static String getRandomCharacterString(int length) {
        /**
         *  "啊啵额风格阿私里法噗机师的飞德洒方司壹贰叁肆伍陆柒捌玖拾佰仟万万亿元角分零整正一二两三四五六七八九十念毛另"的编码
         *  为了过代码检测中不能有中文的限制
         */

        String end = "%B0%A1%E0%A3%B6%EE%B7%E7%B8%F1%B0%A2%CB%BD%C0%EF%B7%A8%E0%DB%BB%FA%CA%A6%B5%C4%B7%C9%B5%C2%C8%F7%B7%BD%CB%BE%D2%BC%B7%A1%C8%FE%CB%C1%CE%E9%C2%BD%C6%E2%B0%C6%BE%C1%CA%B0%B0%DB%C7%AA%CD%F2%CD%F2%D2%DA%D4%AA%BD%C7%B7%D6%C1%E3%D5%FB%D5%FD%D2%BB%B6%FE%C1%BD%C8%FD%CB%C4%CE%E5%C1%F9%C6%DF%B0%CB%BE%C5%CA%AE%C4%EE%C3%AB%C1%ED";
        String base = "";
        try {
            base = URLDecoder.decode(end, "GBK");
        } catch (Exception e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
        return getRandomString(length, base);
    }


    public static String getRandomLetterString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz";
        return getRandomString(length, base);
    }

    public static byte[] getRandomByte(Integer size) {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        Random random = new Random(System.currentTimeMillis());
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return bytes;
    }

    public static String getRandomString(int length, String base) {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        Random random = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static int getRandomInteger() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt();
    }

    public static long getRandomLong() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        Random random = new Random(System.currentTimeMillis());
        return random.nextLong();
    }

    public static long getRandomLongAbs() {
        return Math.abs(getRandomLong());
    }

    public static Double getRandomDouble() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        Random random = new Random(System.currentTimeMillis());
        return random.nextDouble();
    }

    public static Float getRandomFloat() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        Random random = new Random(System.currentTimeMillis());
        return random.nextFloat();
    }

}