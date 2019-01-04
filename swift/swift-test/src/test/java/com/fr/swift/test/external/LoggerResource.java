package com.fr.swift.test.external;

import com.fr.swift.log.SwiftLoggerFactory;
import com.fr.swift.log.SwiftLoggers;
import org.junit.rules.ExternalResource;

/**
 * @author anchore
 * @date 2018/10/25
 */
public class LoggerResource extends ExternalResource {

    private SwiftLoggerFactory<?> loggerFactory;

    @Override
    protected void before() {
        loggerFactory = SwiftLoggers.getLoggerFactory();
//        SwiftLoggers.setLoggerFactory(new SwiftLog4jLoggers());
    }

    @Override
    protected void after() {
        SwiftLoggers.setLoggerFactory(loggerFactory);
    }
}