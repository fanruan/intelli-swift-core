package com.fr.swift.exception;

/**
 * Key名称重复异常
 * Created by Connery on 2015/12/8.
 */
public class FactoryKeyDuplicateException extends Exception{
    private static final long serialVersionUID = -2527198399789152230L;

    public FactoryKeyDuplicateException(String message) {
        super(message);
    }
}