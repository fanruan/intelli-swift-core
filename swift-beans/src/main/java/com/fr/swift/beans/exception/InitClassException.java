package com.fr.swift.beans.exception;

/*
 * This class created on 2019/8/13
 *
 * @author Krysta
 * @description 用于抛出创建bean的时候遇到的问题
 * */
public class InitClassException extends RuntimeException {
    private static final long serialVersionUID = -389954783924756L;

    public InitClassException(String message) {
        super(message);
    }

}
