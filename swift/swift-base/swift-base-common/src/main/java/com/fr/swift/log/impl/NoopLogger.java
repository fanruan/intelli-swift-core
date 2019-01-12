package com.fr.swift.log.impl;

/**
 * @author anchore
 * @date 2019/1/3
 */
public class NoopLogger extends BaseSwiftLogger {

    @Override
    public void debug(String msg, Object... args) {
    }

    @Override
    public void info(String msg, Object... args) {
    }

    @Override
    public void warn(String msg, Object... args) {
    }

    @Override
    public void error(String msg, Object... args) {
    }
}