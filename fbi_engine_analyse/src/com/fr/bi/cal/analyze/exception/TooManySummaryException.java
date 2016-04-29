package com.fr.bi.cal.analyze.exception;


import com.fr.bi.stable.constant.BIBaseConstant;

public class TooManySummaryException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 2721709334026492715L;


    public static int MAX_COUNT = 10000;


    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public String getMessage() {
        return BIBaseConstant.TOO_MANY_SUMARYS;
    }
}