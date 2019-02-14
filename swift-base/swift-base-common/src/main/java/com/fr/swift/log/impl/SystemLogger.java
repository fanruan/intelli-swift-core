package com.fr.swift.log.impl;

import com.fr.swift.structure.Pair;
import com.fr.swift.util.Optional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author anchore
 * @date 2018/10/20
 */
public class SystemLogger extends BaseSwiftLogger {

    private static final String DEBUG = "DEBUG", INFO = "INFO", WARN = "WARN", ERROR = "ERROR";

    private static String now() {
        return new SimpleDateFormat("yy-M-d H:m:s.S").format(new Date());
    }

    private void log(String level, String msg, Object... args) {
        Pair<String, Optional<Throwable>> pair = format(msg, args);
        System.out.printf("%s %s %s %s%n", now(), Thread.currentThread().getName(), level, pair.getKey());
        if (pair.getValue().isPresent()) {
            pair.getValue().get().printStackTrace(System.out);
        }
    }

    @Override
    public void debug(String msg, Object... args) {
        if (isDebugEnabled()) {
            log(DEBUG, msg, args);
        }
    }

    @Override
    public void info(String msg, Object... args) {
        if (isInfoEnabled()) {
            log(INFO, msg, args);
        }
    }

    @Override
    public void warn(String msg, Object... args) {
        if (isWarnEnabled()) {
            log(WARN, msg, args);
        }
    }

    @Override
    public void error(String msg, Object... args) {
        if (isErrorEnabled()) {
            log(ERROR, msg, args);
        }
    }
}