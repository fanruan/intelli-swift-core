package com.fr.swift.log.impl;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.util.Strings;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author anchore
 * @date 2018/7/4
 */
public abstract class BaseSwiftLogger implements SwiftLogger {

    protected static FormattingTuple format(String s, Object... objects) {
        return MessageFormatter.arrayFormat(s, objects);
    }

    @Override
    public void warn(Throwable t) {
        warn(Strings.EMPTY, t);
    }

    @Override
    public void error(Throwable t) {
        error(Strings.EMPTY, t);
    }

    @Override
    public void debug(Throwable t) {
        debug(Strings.EMPTY, t);
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String s) {
    }

    @Override
    public void error(Marker marker, String s, Object o) {
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String s) {
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String s) {
    }

    @Override
    public void info(Marker marker, String s, Object o) {
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String s) {
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String s) {
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
    }
}