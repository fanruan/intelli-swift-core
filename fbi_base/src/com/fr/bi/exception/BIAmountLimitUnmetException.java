package com.fr.bi.exception;

/**
 * Created by Connery on 2015/12/24.
 */
public class BIAmountLimitUnmetException extends Exception {
    private static final long serialVersionUID = -244034779589919177L;

    public BIAmountLimitUnmetException() {
    }

    public BIAmountLimitUnmetException(String message) {

        super(message);
    }
}