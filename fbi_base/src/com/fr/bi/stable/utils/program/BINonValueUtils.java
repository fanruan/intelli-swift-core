package com.fr.bi.stable.utils.program;

import com.fr.bi.exception.BIRuntimeException;

/**
 * Created by Connery on 2015/12/7.
 */
public class BINonValueUtils {

    public static void checkNull(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("the parameter can't be null");
        }
    }

    public static void checkNull(Object arg0, Object... args) {
        checkNull(arg0);
        checkNull(args);
    }

    public static void checkNull(Object[] args) {
        for (Object arg : args) {
            checkNull(arg);
        }
    }

    public static BIRuntimeException beyondControl() {
        return new BIRuntimeException("the responed of FineBI analytic tool is confusion and beyond retrieve");
    }

    public static BIRuntimeException beyondControl(String message) {
        return new BIRuntimeException("the responed of FineBI analytic tool is confusion and beyond retrieve.\r\n" + message);
    }

    public static BIRuntimeException beyondControl(String message, Throwable throwable) {
        return new BIRuntimeException("the responed of FineBI analytic tool is confusion and beyond retrieve.\r\n" + message, throwable);
    }

    public static BIRuntimeException beyondControl(Throwable throwable) {
        if (throwable instanceof BIRuntimeException) {
            Throwable cause = throwable.getCause();
            if (null != cause) {
                return beyondControl(cause.getMessage(), cause);
            } else {
                return beyondControl(throwable.getMessage(), throwable);
            }
        }
        return beyondControl(throwable.getMessage(), throwable);
    }

    public static BIRuntimeException illegalArgument(String message) {
        return new BIRuntimeException(message);
    }


}