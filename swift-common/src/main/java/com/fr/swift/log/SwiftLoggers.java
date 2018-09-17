package com.fr.swift.log;

import com.fr.swift.util.function.Function;

/**
 * @author anchore
 */
public final class SwiftLoggers {
    private static Function<?, SwiftLogger> loggerFactory = new SwiftFrLoggers();

    /**
     *  不用static final SwiftLogger = SwiftLoggers.getLogger();的方式了
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

    public static void setLoggerFactory(Function<?, SwiftLogger> loggerFactory) {
        SwiftLoggers.loggerFactory = loggerFactory;
    }

    public static Function<?, SwiftLogger> getLoggerFactory() {
        return loggerFactory;
    }
}