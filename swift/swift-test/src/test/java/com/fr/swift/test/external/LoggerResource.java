package com.fr.swift.test.external;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.function.Function;
import org.junit.rules.ExternalResource;

/**
 * @author anchore
 * @date 2018/10/25
 */
public class LoggerResource extends ExternalResource {

    private Function<?, SwiftLogger> loggerFactory;

    @Override
    protected void before() {
//        loggerFactory = SwiftLoggers.getLoggerFactory();
//        SwiftLoggers.setLoggerFactory(new SwiftLog4jLoggers());
    }

    @Override
    protected void after() {
        SwiftLoggers.setLoggerFactory(loggerFactory);
    }
}