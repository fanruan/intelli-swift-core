package com.fr.swift.log;

import com.fr.swift.log.impl.SystemLoggers;

/**
 * @author anchore
 */
public final class SwiftLoggers {

    private static SwiftLoggerFactory<?> loggerFactory = new SystemLoggers();

    /**
     * 不用static final SwiftLogger = SwiftLoggers.getLogger();的方式了
     * 推荐直接SwiftLoggers.getLogger().error(e);
     */
    public static SwiftLogger getLogger() {
        return loggerFactory.apply(null);
    }

    /**
     * @deprecated 不用传class啦
     */
    @Deprecated
    public static SwiftLogger getLogger(Class cls) {
        return getLogger();
    }

    public static void setLoggerFactory(SwiftLoggerFactory<?> loggerFactory) {
        SwiftLoggers.loggerFactory = loggerFactory;
    }

    public static SwiftLoggerFactory<?> getLoggerFactory() {
        return loggerFactory;
    }
}