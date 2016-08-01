package com.fr.bi.stable.utils.program;

import com.fr.bi.exception.BIRuntimeException;

/**
 * Created by Connery on 2015/12/7.
 */
public class BINonValueUtils {

    public static void checkNull(Object... args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                throw new IllegalArgumentException("the parameter :" + args.toString() + " can't be null");
            }
        }
    }

    public static BIRuntimeException beyondControl() {
        return new BIRuntimeException("the status of FineBI analytic tool is confusion and beyond retrieve");
    }

    public static BIRuntimeException beyondControl(String message) {
        return new BIRuntimeException("the status of FineBI analytic tool is confusion and beyond retrieve.\r\n" + message);
    }

    public static BIRuntimeException beyondControl(String message, Throwable throwable) {
        return new BIRuntimeException("the status of FineBI analytic tool is confusion and beyond retrieve.\r\n" + message, throwable);
    }

    public static BIRuntimeException beyondControl(Throwable throwable) {
        if (throwable instanceof BIRuntimeException) {
            Throwable cause = throwable.getCause();
            return beyondControl(cause.getMessage(), cause);
        }
        return beyondControl(throwable.getMessage(), throwable);
    }

    public static BIRuntimeException illegalArgument(String message) {
        return new BIRuntimeException(message);
    }


}