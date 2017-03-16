package com.finebi.cube.exception;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeResourceUnavailableException extends Exception {
    private static final long serialVersionUID = 3288045782447888924L;

    public BICubeResourceUnavailableException(String message) {
        super(message);
    }

    public BICubeResourceUnavailableException() {
    }
}
