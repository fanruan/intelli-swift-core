package com.fr.swift.basics.exception;

import com.fr.swift.basics.ProcessHandler;

/**
 * @author yee
 * @date 2018/10/24
 */
public class ClassIsNotInterfaceException extends RuntimeException {
    public ClassIsNotInterfaceException(Class<? extends ProcessHandler> pClass) {
        super(String.format("%s is not interface", pClass.getName()));
    }
}
