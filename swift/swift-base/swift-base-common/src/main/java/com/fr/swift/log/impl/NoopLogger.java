package com.fr.swift.log.impl;

/**
 * @author anchore
 * @date 2019/1/3
 */
public class NoopLogger extends BaseSwiftLogger {
    @Override
    public String getName() {
        return getClass().getSimpleName();
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
    }

    @Override
    public void debug(String s, Object o) {
    }

    @Override
    public void debug(String s, Object o, Object o1) {
    }

    @Override
    public void debug(String s, Object... objects) {
    }

    @Override
    public void debug(String s, Throwable throwable) {
    }

    @Override
    public void info(String s) {
    }

    @Override
    public void info(String s, Object o) {
    }

    @Override
    public void info(String s, Object o, Object o1) {
    }

    @Override
    public void info(String s, Object... objects) {
    }

    @Override
    public void info(String s, Throwable throwable) {
    }

    @Override
    public void warn(String s) {
    }

    @Override
    public void warn(String s, Object o) {
    }

    @Override
    public void warn(String s, Object... objects) {
    }

    @Override
    public void warn(String s, Object o, Object o1) {
    }

    @Override
    public void warn(String s, Throwable throwable) {
    }

    @Override
    public void error(String s) {
    }

    @Override
    public void error(String s, Object o) {
    }

    @Override
    public void error(String s, Object o, Object o1) {
    }

    @Override
    public void error(String s, Object... objects) {
    }

    @Override
    public void error(String s, Throwable throwable) {
    }
}