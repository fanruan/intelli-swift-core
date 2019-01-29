package com.fr.swift.log.impl;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Optional;
import com.fr.swift.util.Strings;

/**
 * @author anchore
 * @date 2018/7/4
 */
public abstract class BaseSwiftLogger implements SwiftLogger {

    protected Pair<String, Optional<Throwable>> format(String s, Object... objects) {
        return MsgFmt.fmt(s, objects);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String msg) {
        debug(msg, (Object[]) null);
    }

    @Override
    public void debug(Throwable t) {
        debug(Strings.EMPTY, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        info(msg, (Object[]) null);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
        warn(msg, (Object[]) null);
    }

    @Override
    public void warn(Throwable t) {
        warn(Strings.EMPTY, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    public void error(String s, Throwable throwable) {
        error(s, new Object[]{throwable});
    }

    @Override
    public void error(String msg) {
        error(msg, (Object[]) null);
    }

    @Override
    public void error(Throwable t) {
        error(Strings.EMPTY, t);
    }
}