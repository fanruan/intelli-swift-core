package com.fr.swift.jdbc.exception;

/**
 * @author yee
 * @date 2018/8/26
 */
public class NoCodecResponseException extends RuntimeException {
    private static final long serialVersionUID = -7937742622810911418L;

    NoCodecResponseException() {
        super("Cannot find response");
    }
}
