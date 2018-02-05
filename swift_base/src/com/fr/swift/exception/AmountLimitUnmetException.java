package com.fr.swift.exception;

/**
 * Created by Connery on 2015/12/24.
 */
public class AmountLimitUnmetException extends Exception {
    private static final long serialVersionUID = -244034779589919177L;

    public AmountLimitUnmetException() {
    }

    public AmountLimitUnmetException(String message) {

        super(message);
    }
}