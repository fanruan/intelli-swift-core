package com.fr.swift.log;

import com.fr.log.FineLoggerFactory;

/**
 * @author anchore
 * @date 2018/7/4
 */
class SwiftFrLogger extends BaseSwiftLogger implements SwiftLogger {
    SwiftFrLogger() {
    }

    @Override
    public String getName() {
        return FineLoggerFactory.getLogger().toString();
    }

    @Override
    public void trace(String s) {
    }

    @Override
    public void trace(String s, Object o) {
    }

    @Override
    public void trace(String s, Object o, Object o1) {
    }

    @Override
    public void trace(String s, Object... objects) {
    }

    @Override
    public void trace(String s, Throwable throwable) {
    }

    @Override
    public void debug(String s) {
        FineLoggerFactory.getLogger().debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        FineLoggerFactory.getLogger().debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        FineLoggerFactory.getLogger().debug(s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        FineLoggerFactory.getLogger().debug(s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        FineLoggerFactory.getLogger().debug(s, throwable);
    }

    @Override
    public void info(String s) {
        FineLoggerFactory.getLogger().info(s);
    }

    @Override
    public void info(String s, Object o) {
        FineLoggerFactory.getLogger().info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        FineLoggerFactory.getLogger().info(s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        FineLoggerFactory.getLogger().info(s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        FineLoggerFactory.getLogger().info(s, throwable);
    }

    @Override
    public void warn(String s) {
        FineLoggerFactory.getLogger().warn(s);
    }

    @Override
    public void warn(String s, Object o) {
        FineLoggerFactory.getLogger().warn(format(s, o));
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        FineLoggerFactory.getLogger().warn(format(s, o, o1));
    }

    @Override
    public void warn(String s, Object... objects) {
        FineLoggerFactory.getLogger().warn(format(s, objects));
    }

    @Override
    public void warn(String s, Throwable throwable) {
        FineLoggerFactory.getLogger().warn(s, throwable);
    }

    @Override
    public void error(String s) {
        FineLoggerFactory.getLogger().error(s);
    }

    @Override
    public void error(String s, Object o) {
        FineLoggerFactory.getLogger().error(format(s, o));
    }

    @Override
    public void error(String s, Object o, Object o1) {
        FineLoggerFactory.getLogger().error(format(s, o, o1));
    }

    @Override
    public void error(String s, Object... objects) {
        FineLoggerFactory.getLogger().error(format(s, objects));
    }

    @Override
    public void error(String s, Throwable throwable) {
        FineLoggerFactory.getLogger().error(s, throwable);
    }

    @Override
    public Type getType() {
        return Type.FR;
    }
}