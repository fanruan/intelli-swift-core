package com.fr.swift.source.core;


import com.fr.swift.log.SwiftLoggers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 小灰灰 on 2014/8/13.
 */
public class MD5Utils {
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static MessageDigest getMessageDigest() {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e.getMessage(), e);
        }
        return digest;
    }

    public static String getMD5String(byte[] bytes) {
        int len = bytes.length;
        char[] str = new char[len * 2];
        int j = 0;
        for (int i = 0; i < len; i++) {
            byte b = bytes[i];
            str[j++] = CHARS[b >>> 4 & 0xf];
            str[j++] = CHARS[b & 0xf];
        }
        return new String(str).substring(12, 20);
    }

    public static String getMD5String(String[] strings) {
        MessageDigest digest = getMessageDigest();
        for (int i = 0, len = strings.length; i < len; i++) {
            try {
                digest.update(strings[i].getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        return getMD5String(digest.digest());
    }

    public static String getMD5String(List<String> strings) {
        MessageDigest digest = getMessageDigest();
        Iterator<String> it = strings.iterator();
        while (it.hasNext()) {
            try {
                digest.update(it.next().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        return getMD5String(digest.digest());
    }
}