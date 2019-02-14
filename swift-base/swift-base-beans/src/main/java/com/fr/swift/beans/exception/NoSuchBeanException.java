package com.fr.swift.beans.exception;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class NoSuchBeanException extends RuntimeException {
    private static final long serialVersionUID = -3899519283969980656L;

    public NoSuchBeanException(String beanName) {
        super(beanName + "'s bean is not defined!");
    }
}
