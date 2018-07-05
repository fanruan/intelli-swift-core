package com.fr.swift.log;

import com.fr.third.apache.log4j.Logger;
import com.fr.third.apache.log4j.Priority;

/**
 * @author anchore
 * @date 2018/7/4
 */
class SwiftLog4jLogger extends BaseSwiftLogger implements SwiftLogger {
    private Logger logger;
    String FQCN = "";
    private boolean traceCapable;


    SwiftLog4jLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        if (isTraceEnabled()) {
            logger.trace(s);
        }
    }

    @Override
    public void trace(String s, Object o) {
        if (isTraceEnabled()) {
            logger.trace(format(s, o));
        }
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        if (isTraceEnabled()) {
            logger.trace(format(s, o, o1));
        }
    }

    @Override
    public void trace(String s, Object... objects) {
        if (isTraceEnabled()) {
            logger.trace(format(s, objects));
        }
    }

    @Override
    public void trace(String s, Throwable throwable) {
        if (isTraceEnabled()) {
            logger.trace(s, throwable);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        if (isDebugEnabled()) {
            logger.debug(s);
        }
    }

    @Override
    public void debug(String s, Object o) {
        if (isDebugEnabled()) {
            logger.debug(format(s, o));
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if (isDebugEnabled()) {
            logger.debug(format(s, o, o1));
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        if (isDebugEnabled()) {
            logger.debug(format(s, objects));
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (isDebugEnabled()) {
            logger.debug(s, throwable);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        if (isInfoEnabled()) {
            logger.info(s);
        }
    }

    @Override
    public void info(String s, Object o) {
        if (isInfoEnabled()) {
            logger.info(format(s, o));
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        if (isInfoEnabled()) {
            logger.info(format(s, o, o1));
        }
    }

    @Override
    public void info(String s, Object... objects) {
        if (isInfoEnabled()) {
            logger.info(format(s, objects));
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        if (isInfoEnabled()) {
            logger.info(s, throwable);
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isEnabledFor(Priority.WARN);
    }

    @Override
    public void warn(String s) {
        if (isWarnEnabled()) {
            logger.warn(s);
        }
    }

    @Override
    public void warn(String s, Object o) {
        if (isWarnEnabled()) {
            logger.warn(format(s, o));
        }
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        if (isWarnEnabled()) {
            logger.warn(format(s, o, o1));
        }
    }

    @Override
    public void warn(String s, Object... objects) {
        if (isWarnEnabled()) {
            logger.warn(format(s, objects));
        }
    }

    @Override
    public void warn(String s, Throwable throwable) {
        if (isWarnEnabled()) {
            logger.warn(s, throwable);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isEnabledFor(Priority.ERROR);
    }

    @Override
    public void error(String s) {
        if (isErrorEnabled()) {
            logger.error(s);
        }
    }

    @Override
    public void error(String s, Object o) {
        if (isErrorEnabled()) {
            logger.error(format(s, o));
        }
    }

    @Override
    public void error(String s, Object o, Object o1) {
        if (isErrorEnabled()) {
            logger.error(format(s, o, o1));
        }
    }

    @Override
    public void error(String s, Object... objects) {
        if (isErrorEnabled()) {
            logger.error(format(s, objects));
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        if (isErrorEnabled()) {
            logger.error(s, throwable);
        }
    }

    @Override
    public Type getType() {
        return Type.LOG4J;
    }


//
//    private boolean isTraceCapable() {
//        try {
//            this.logger.isTraceEnabled();
//            return true;
//        } catch (NoSuchMethodError var2) {
//            return false;
//        }
//    }
//
//    public boolean isTraceEnabled() {
//        return this.traceCapable ? this.logger.isTraceEnabled() : this.logger.isDebugEnabled();
//    }
//
//    public void trace(String msg) {
//        this.logger.log(FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, msg, (Throwable)null);
//    }
//
//    public void trace(String format, Object arg) {
//        if (this.isTraceEnabled()) {
//            FormattingTuple ft = MessageFormatter.format(format, arg);
//            this.logger.log(FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void trace(String format, Object arg1, Object arg2) {
//        if (this.isTraceEnabled()) {
//            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
//            this.logger.log(FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void trace(String format, Object... arguments) {
//        if (this.isTraceEnabled()) {
//            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
//            this.logger.log(FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void trace(String msg, Throwable t) {
//        this.logger.log(FQCN, this.traceCapable ? Level.TRACE : Level.DEBUG, msg, t);
//    }
//
//    public boolean isDebugEnabled() {
//        return this.logger.isDebugEnabled();
//    }
//
//    public void debug(String msg) {
//        this.logger.log(FQCN, Level.DEBUG, msg, (Throwable)null);
//    }
//
//    public void debug(String format, Object arg) {
//        if (this.logger.isDebugEnabled()) {
//            FormattingTuple ft = MessageFormatter.format(format, arg);
//            this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void debug(String format, Object arg1, Object arg2) {
//        if (this.logger.isDebugEnabled()) {
//            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
//            this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void debug(String format, Object... arguments) {
//        if (this.logger.isDebugEnabled()) {
//            FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
//            this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void debug(String msg, Throwable t) {
//        this.logger.log(FQCN, Level.DEBUG, msg, t);
//    }
//
//    public boolean isInfoEnabled() {
//        return this.logger.isInfoEnabled();
//    }
//
//    public void info(String msg) {
//        this.logger.log(FQCN, Level.INFO, msg, (Throwable)null);
//    }
//
//    public void info(String format, Object arg) {
//        if (this.logger.isInfoEnabled()) {
//            FormattingTuple ft = MessageFormatter.format(format, arg);
//            this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void info(String format, Object arg1, Object arg2) {
//        if (this.logger.isInfoEnabled()) {
//            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
//            this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void info(String format, Object... argArray) {
//        if (this.logger.isInfoEnabled()) {
//            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
//            this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void info(String msg, Throwable t) {
//        this.logger.log(FQCN, Level.INFO, msg, t);
//    }
//
//    public boolean isWarnEnabled() {
//        return this.logger.isEnabledFor(Level.WARN);
//    }
//
//    public void warn(String msg) {
//        this.logger.log(FQCN, Level.WARN, msg, (Throwable)null);
//    }
//
//    public void warn(String format, Object arg) {
//        if (this.logger.isEnabledFor(Level.WARN)) {
//            FormattingTuple ft = MessageFormatter.format(format, arg);
//            this.logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void warn(String format, Object arg1, Object arg2) {
//        if (this.logger.isEnabledFor(Level.WARN)) {
//            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
//            this.logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void warn(String format, Object... argArray) {
//        if (this.logger.isEnabledFor(Level.WARN)) {
//            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
//            this.logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void warn(String msg, Throwable t) {
//        this.logger.log(FQCN, Level.WARN, msg, t);
//    }
//
//    public boolean isErrorEnabled() {
//        return this.logger.isEnabledFor(Level.ERROR);
//    }
//
//    public void error(String msg) {
//        this.logger.log(FQCN, Level.ERROR, msg, (Throwable)null);
//    }
//
//    public void error(String format, Object arg) {
//        if (this.logger.isEnabledFor(Level.ERROR)) {
//            FormattingTuple ft = MessageFormatter.format(format, arg);
//            this.logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void error(String format, Object arg1, Object arg2) {
//        if (this.logger.isEnabledFor(Level.ERROR)) {
//            FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
//            this.logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void error(String format, Object... argArray) {
//        if (this.logger.isEnabledFor(Level.ERROR)) {
//            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
//            this.logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
//        }
//
//    }
//
//    public void error(String msg, Throwable t) {
//        this.logger.log(FQCN, Level.ERROR, msg, t);
//    }
//
//    public void log(Marker marker, String callerFQCN, int level, String msg, Object[] argArray, Throwable t) {
//        Level log4jLevel = this.toLog4jLevel(level);
//        this.logger.log(callerFQCN, log4jLevel, msg, t);
//    }
//
//    private Level toLog4jLevel(int level) {
//        Level log4jLevel;
//        switch(level) {
//            case 0:
//                log4jLevel = this.traceCapable ? Level.TRACE : Level.DEBUG;
//                break;
//            case 10:
//                log4jLevel = Level.DEBUG;
//                break;
//            case 20:
//                log4jLevel = Level.INFO;
//                break;
//            case 30:
//                log4jLevel = Level.WARN;
//                break;
//            case 40:
//                log4jLevel = Level.ERROR;
//                break;
//            default:
//                throw new IllegalStateException("Level number " + level + " is not recognized.");
//        }
//
//        return log4jLevel;
//    }

//    public void log(LoggingEvent event) {
//        Level log4jLevel = this.toLog4jLevel(event.getLevel().toInt());
//        if (this.logger.isEnabledFor(log4jLevel)) {
//            spi.LoggingEvent log4jevent = this.toLog4jEvent(event, log4jLevel);
//            this.logger.callAppenders(log4jevent);
//        }
//    }
//
//    private LoggingEvent toLog4jEvent(LoggingEvent event, Level log4jLevel) {
//        FormattingTuple ft = MessageFormatter.format(event.getMessage(), event.getArgumentArray(), event.getThrowable());
//        LocationInfo locationInfo = new LocationInfo("NA/SubstituteLogger", "NA/SubstituteLogger", "NA/SubstituteLogger", "0");
//        ThrowableInformation ti = null;
//        Throwable t = ft.getThrowable();
//        if (t != null) {
//            ti = new ThrowableInformation(t);
//        }
//
//        LoggingEvent log4jEvent = new LoggingEvent(FQCN, this.logger, event.getTimeStamp(), log4jLevel, ft.getMessage(), event.getThreadName(), ti, (String)null, locationInfo, (Map)null);
//        return log4jEvent;
//    }
}