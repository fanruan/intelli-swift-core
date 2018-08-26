package com.fr.swift.jdbc.exception;

/**
 * @author yee
 * @date 2018/8/26
 */
public class URLFormatException extends RuntimeException {
    public URLFormatException(String url) {
        super(url + " is not valid");
    }
}
