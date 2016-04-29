package com.fr.bi.stable.utils.program;

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

    public static RuntimeException beyondControl() {
        return new RuntimeException("the status of FineBI analytic tool is confusion and beyond retrieve");
    }

    public static RuntimeException beyondControl(String message) {
        return new RuntimeException("the status of FineBI analytic tool is confusion and beyond retrieve.\r\n" + message);
    }

    public static RuntimeException beyondControl(String message, Throwable throwable) {
        return new RuntimeException("the status of FineBI analytic tool is confusion and beyond retrieve.\r\n" + message, throwable);
    }

    public static RuntimeException beyondControl(Throwable throwable) {
        return beyondControl(throwable.getMessage(), throwable);
    }

    public static RuntimeException illegalArgument(String message) {
        return new IllegalArgumentException(message);
    }
}