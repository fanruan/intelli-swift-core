package com.fr.swift.log;

import com.fr.swift.log.impl.BaseSwiftLogger;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Optional;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author anchore
 * @date 2018/7/4
 */
class SwiftLog4jLogger extends BaseSwiftLogger implements SwiftLogger {

    private static final String FQCN = SwiftLog4jLogger.class.getName();

    private Logger logger;

    SwiftLog4jLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String s, Object... objects) {
        if (isDebugEnabled()) {
            Pair<String, Optional<Throwable>> pair = format(s, objects);
            logger.log(FQCN, Level.DEBUG, pair.getKey(), pair.getValue().orElse(null));
        }
    }

    @Override
    public void debug(String msg) {
        super.debug(msg);
    }

    @Override
    public void debug(Throwable t) {
        super.debug(t);
    }

    @Override
    public void info(String s, Object... objects) {
        if (isInfoEnabled()) {
            Pair<String, Optional<Throwable>> pair = format(s, objects);
            logger.log(FQCN, Level.INFO, pair.getKey(), pair.getValue().orElse(null));
        }
    }

    @Override
    public void info(String msg) {
        super.info(msg);
    }

    @Override
    public void warn(String s, Object... objects) {
        if (isWarnEnabled()) {
            Pair<String, Optional<Throwable>> pair = format(s, objects);
            logger.log(FQCN, Level.WARN, pair.getKey(), pair.getValue().orElse(null));
        }
    }

    @Override
    public void warn(String msg) {
        super.warn(msg);
    }

    @Override
    public void warn(Throwable t) {
        super.warn(t);
    }

    @Override
    public void error(String s, Object... objects) {
        if (isErrorEnabled()) {
            Pair<String, Optional<Throwable>> pair = format(s, objects);
            logger.log(FQCN, Level.ERROR, pair.getKey(), pair.getValue().orElse(null));
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        super.error(s, throwable);
    }

    @Override
    public void error(String msg) {
        super.error(msg);
    }

    @Override
    public void error(Throwable t) {
        super.error(t);
    }
}