package com.finebi.cube.exception;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeResourceAbsentException extends Exception {
    private static final long serialVersionUID = -2848946395171347908L;

    public BICubeResourceAbsentException(String message) {
        super(message);
    }

    public BICubeResourceAbsentException() {
    }
}
