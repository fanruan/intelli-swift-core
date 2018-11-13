package com.fr.swift.exception;

/**
 * @author yee
 * @date 2018/2/7
 */
public class CubePathEmptyException extends RuntimeException {
    public CubePathEmptyException() {
        super("Cube path empty");
    }
}
