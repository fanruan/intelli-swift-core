package com.fr.bi.stable.exception;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITablePathConfusionException extends Exception {
    private static final long serialVersionUID = 5638442319948238374L;

    public BITablePathConfusionException() {
    }

    public BITablePathConfusionException(String message) {
        super(message);
    }
}