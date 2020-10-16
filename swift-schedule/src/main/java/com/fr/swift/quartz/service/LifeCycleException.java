package com.fr.swift.quartz.service;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/9/3
 */
public class LifeCycleException extends RuntimeException {

    private static final long serialVersionUID = 1911228565488578827L;

    public LifeCycleException(String message) {
        super(message);
    }

    public LifeCycleException(Throwable cause) {
        super(cause);
    }
}
