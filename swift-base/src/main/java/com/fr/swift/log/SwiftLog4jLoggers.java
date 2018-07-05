package com.fr.swift.log;

import com.fr.swift.util.function.Function;
import com.fr.third.apache.log4j.Logger;

/**
 * @author anchore
 * @date 2018/7/4
 */
public class SwiftLog4jLoggers implements Function<Void, SwiftLogger> {
    private static final SwiftLogger LOGGER = newLogger();

    private static SwiftLogger newLogger() {
        Logger logger = Logger.getLogger(SwiftLog4jLogger.class);
        return new SwiftLog4jLogger(logger);
    }

    @Override
    public SwiftLogger apply(Void p) {
        return LOGGER;
    }
}