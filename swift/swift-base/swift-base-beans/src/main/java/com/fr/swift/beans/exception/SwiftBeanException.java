package com.fr.swift.beans.exception;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftBeanException extends RuntimeException {
    private static final long serialVersionUID = -4719858782164185231L;

    public SwiftBeanException(String message) {
        super(message);
    }

    public SwiftBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    public SwiftBeanException(Throwable cause) {
        super(cause);
    }

    public SwiftBeanException(String beanName, Type type) {
        super("Swift bean name :" + beanName + " is " + type);
    }

    public enum Type {
        EXIST, NOT_EXIST
    }
}
