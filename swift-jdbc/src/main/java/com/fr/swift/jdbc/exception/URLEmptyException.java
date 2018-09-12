package com.fr.swift.jdbc.exception;

/**
 * @author yee
 * @date 2018/8/26
 */
public class URLEmptyException extends RuntimeException {
    public URLEmptyException() {
        super("URL can not be null!");
    }
}
