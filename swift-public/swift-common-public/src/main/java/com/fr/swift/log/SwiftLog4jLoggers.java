package com.fr.swift.log;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;

/**
 * @author anchore
 * @date 2018/7/4
 */
public class SwiftLog4jLoggers implements SwiftLoggerFactory<Void> {

    private static final SwiftLogger LOGGER = newLogger();

    private static SwiftLogger newLogger() {
        Logger logger = Logger.getLogger(SwiftLog4jLogger.class);
        logger.setLevel(Level.INFO);
        logger.setAdditivity(false);
        PatternLayout layout = new PatternLayout("%d{yy-M-d H:m:s.S} %t %p [%C{1}.%M] %m%n");
        logger.addAppender(new ConsoleAppender(layout));
        try {
            logger.addAppender(new DailyRollingFileAppender(layout, "logs/swift", "yy-MM-dd'.log'"));
        } catch (IOException ignore) {
        }
        return new SwiftLog4jLogger(logger);
    }

    @Override
    public SwiftLogger apply(Void p) {
        return LOGGER;
    }
}