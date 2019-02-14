package com.fr.swift.redis;

/**
 * This class created on 2018/6/21
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftRedisException extends RuntimeException {

    public SwiftRedisException() {
    }

    public SwiftRedisException(String message) {
        super(message);
    }

    public SwiftRedisException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwiftRedisException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
