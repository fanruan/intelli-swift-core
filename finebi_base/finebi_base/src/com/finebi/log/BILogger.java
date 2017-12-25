package com.finebi.log;

import com.finebi.log.helpers.FormattingTuple;
import com.finebi.log.helpers.MessageFormatter;
import com.fr.third.apache.log4j.Level;
import com.fr.third.apache.log4j.Logger;

/**
 * This class created on 2016/10/9.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class BILogger {
    private final static String FQCN = BILogger.class.getName();

    private Logger logger;
    private Logger poolLogger = Logger.getLogger("LogVisible");

    public BILogger(Logger logger) {
//        BINonValueUtils.checkNull(logger);
        this.logger = logger;
    }

    public String getName() {
        return logger.getName();
    }

    public void trace(String msg) {
        logger.log(FQCN, Level.TRACE, msg, null);
    }

    public void trace(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.TRACE, ft.getMessage(), ft.getThrowable());
    }

    public void trace(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        logger.log(FQCN, Level.TRACE, ft.getMessage(), ft.getThrowable());
    }

    public void trace(String format, Object[] arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        logger.log(FQCN, Level.TRACE, ft.getMessage(), ft.getThrowable());
    }

    public void trace(String msg, Throwable throwable) {
        logger.log(FQCN, Level.TRACE, msg, throwable);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void debug(String msg) {
        logger.log(FQCN, Level.DEBUG, msg, null);
    }

    public void debug(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
    }

    public void debug(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
    }

    public void debug(String format, Object[] arguments) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
        logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
    }

    public void debug(String msg, Throwable throwable) {
        logger.log(FQCN, Level.DEBUG, msg, throwable);
    }

    public void info(String msg) {
        logger.log(FQCN, Level.INFO, msg, null);
    }

    public void infoSticky(String msg) {
        logger.log(FQCN, Level.INFO, msg, null);
        poolLogger.log(FQCN, Level.INFO, msg, null);
    }

    public void info(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
    }

    public void infoSticky(String format, Object o) {
        FormattingTuple ft = MessageFormatter.format(format, o);
        logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
        poolLogger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
    }

    public void infoCache(String format, Object arg) {
        if (!BILogCache.getInstance().containsKey(format)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
        }
    }

    public void infoCache(String msg) {
        if (!BILogCache.getInstance().containsKey(msg)) {
            logger.log(FQCN, Level.INFO, msg, null);
            poolLogger.log(FQCN, Level.INFO, msg, null);
        }
    }

    public void info(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
    }

    public void info(String format, Object[] argArray) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
        logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
    }

    public void info(String msg, Throwable throwable) {
        logger.log(FQCN, Level.INFO, msg, throwable);
    }

    public void warn(String msg) {
        logger.log(FQCN, Level.WARN, msg, null);
    }

    public void warnSticky(String msg) {
        logger.log(FQCN, Level.WARN, msg, null);
        poolLogger.log(FQCN, Level.WARN, msg, null);
    }

    public void warn(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
    }


    public void warnSticky(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
        poolLogger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
    }

    public void warnCache(String format, Object arg) {
        if (!BILogCache.getInstance().containsKey(format)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
        }
    }

    public void warnCache(String msg) {
        if (!BILogCache.getInstance().containsKey(msg)) {
            logger.log(FQCN, Level.WARN, msg, null);
        }
    }

    public void warn(String format, Object[] argArray) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
        logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
    }

    public void warn(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
    }

    public void warn(String msg, Throwable throwable) {
        logger.log(FQCN, Level.WARN, msg, throwable);
    }

    public void error(String msg) {
        logger.log(FQCN, Level.ERROR, msg, null);
    }

    public void errorSticky(String msg) {
        logger.log(FQCN, Level.ERROR, msg, null);
        poolLogger.log(FQCN, Level.ERROR, msg, null);
    }

    public void errorCache(String format, Object arg) {
        if (!BILogCache.getInstance().containsKey(format)) {
            FormattingTuple ft = MessageFormatter.format(format, arg);
            logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
        }
    }

    public void error(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
    }

    public void errorSticky(String format, Object arg) {
        FormattingTuple ft = MessageFormatter.format(format, arg);
        logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
        poolLogger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());

    }

    public void errorCache(String msg) {
        if (!BILogCache.getInstance().containsKey(msg)) {
            error(msg);
        }
    }

    public void error(String format, Object arg1, Object arg2) {
        FormattingTuple ft = MessageFormatter.format(format, arg1, arg2);
        logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
    }

    public void error(String format, Object[] argArray) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
        logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
    }

    public void error(String msg, Throwable throwable) {
        logger.log(FQCN, Level.ERROR, msg, throwable);
    }
}
