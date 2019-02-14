package com.fr.swift.repository.exception;

/**
 * @author yee
 * @date 2018-12-06
 */
public class DefaultRepoNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 4994970546835680763L;

    public DefaultRepoNotFoundException(Throwable t) {
        super(t);
    }
}
