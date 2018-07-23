package com.fr.swift.utils;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MonitorUtil {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(MonitorUtil.class);

    private static ThreadLocal<Long> tl = new ThreadLocal<Long>();

    public static void start() {
        tl.set(System.currentTimeMillis());
    }

    public static void finish(String methodName) {
        long finishTime = System.currentTimeMillis();
        LOGGER.debug("method " + methodName + " cost time:" + (finishTime - tl.get()) + "ms");
    }
}
