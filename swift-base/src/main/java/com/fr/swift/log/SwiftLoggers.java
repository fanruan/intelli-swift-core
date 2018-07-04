package com.fr.swift.log;

/**
 * @author anchore
 */
public final class SwiftLoggers {
    private static final SwiftLogger LOGGER = new SwiftLogger();

    public static SwiftLogger getLogger() {
        return LOGGER;
    }

    /**
     * @deprecated 不用传class啦
     */
    @Deprecated
    public static SwiftLogger getLogger(Class cls) {
        return getLogger();
    }
}