package com.fr.swift.query.session.exception;

/**
 * @author yee
 * @date 2018/6/19
 */
public class SessionClosedException extends RuntimeException {
    public SessionClosedException(String sessionId) {
        super(String.format("Session which id is '%s' had bean closed!", sessionId));
    }
}
