package com.fr.swift.repository.exception;

/**
 * @author yee
 * @date 2018-12-06
 */
public class RepoNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 4994970546835680763L;

    public RepoNotFoundException(String message) {
        super(message);
    }
}
