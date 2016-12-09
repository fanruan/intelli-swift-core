package com.fr.bi.common.persistent.json.generator;

/**
 * Created by Connery on 2016/1/20.
 */
public class BIKeyFieldSizeException extends Exception {
    private static final long serialVersionUID = -5201596638637806144L;

    public BIKeyFieldSizeException(String message) {
        super(message);
    }

    public BIKeyFieldSizeException() {
    }
}