package com.fr.swift.log;

import com.fr.swift.util.function.Function;
import com.fr.third.apache.log4j.ConsoleAppender;
import com.fr.third.apache.log4j.DailyRollingFileAppender;
import com.fr.third.apache.log4j.Level;
import com.fr.third.apache.log4j.Logger;
import com.fr.third.apache.log4j.PatternLayout;

import java.io.IOException;

/**
 * @author anchore
 * @date 2018/7/4
 */
public class SwiftLog4jLoggers implements Function<Void, SwiftLogger> {
    private static final SwiftLogger LOGGER = newLogger();

    private static SwiftLogger newLogger() {
        Logger logger = Logger.getLogger(SwiftLog4jLogger.class);
        logger.setLevel(Level.INFO);
        logger.setAdditivity(false);
        PatternLayout layout = new PatternLayout("%d{yy-M-d H:m:s.S} %t %p [%C{1}.%M] %m%n");
        logger.addAppender(new ConsoleAppender(layout));
        try {
            logger.addAppender(new DailyRollingFileAppender(layout, "logs/swift.log", "'.'yy-M-d"));
        } catch (IOException ignore) {
        }
        return new SwiftLog4jLogger(logger);
    }

    @Override
    public SwiftLogger apply(Void p) {
        return LOGGER;
    }
}