package com.fr.bi.stable.exception;

/**
 * Created by Connery on 2016/1/13.
 */
public class BITableRelationConfusionException extends Exception {
    private static final long serialVersionUID = 8875000828361964250L;

    public BITableRelationConfusionException() {
    }

    public BITableRelationConfusionException(String message) {
        super(message);
    }
}