package com.fr.swift.log;

import com.fineio.logger.FineIOLogger;
import com.fr.swift.log.impl.BaseSwiftLogger;

/**
 * @author anchore
 * @date 2019/1/28
 */
public class FineIoLogger extends BaseSwiftLogger implements FineIOLogger, SwiftLogger {

    private SwiftLogger loggerDelegate;

    public FineIoLogger() {
        this(SwiftLoggers.getLogger());
    }

    private FineIoLogger(SwiftLogger swiftLogger) {
        this.loggerDelegate = swiftLogger;
    }

    @Override
    public void debug(String msg, Object... args) {
        loggerDelegate.debug(msg, args);
    }

    @Override
    public void info(String msg, Object... args) {
        loggerDelegate.info(msg, args);
    }

    @Override
    public void warn(String msg, Object... args) {
        loggerDelegate.warn(msg, args);
    }

    @Override
    public void error(String msg, Object... args) {
        loggerDelegate.error(msg, args);
    }
}