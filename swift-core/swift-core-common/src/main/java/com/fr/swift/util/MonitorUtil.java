package com.fr.swift.util;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MonitorUtil {

    private static ThreadLocal<Long> tl = new ThreadLocal<Long>();

    public static void start() {
        tl.set(System.currentTimeMillis());
    }

    public static void finish(String methodName) {
//        SwiftLoggers.getLogger().debug("Invoke {} costs {} ms", methodName, System.currentTimeMillis() - tl.get());
        tl.remove();
    }
}
