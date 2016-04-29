package com.fr.bi.stable.utils.code;

/**
 * Created by Connery on 2015/12/9.
 */
public class BILogDelegate {
    public static void errorDelegate(String message, Throwable e) {
        BILogger.getLogger().error(message, e);
    }

}