package com.fr.bi.exception;

/**
 * Created by Connery on 2015/12/24.
 */
public class BIAmountLimitUnmetException extends Exception {
    public BIAmountLimitUnmetException() {
    }

    public BIAmountLimitUnmetException(String message) {

        super(message);
    }
}