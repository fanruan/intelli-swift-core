package com.fr.swift.basics.exception;

import com.fr.swift.basics.ProcessHandler;

/**
 * @author yee
 * @date 2018/10/24
 */
public class ProcessHandlerNotRegisterException extends RuntimeException {
    public ProcessHandlerNotRegisterException(Class<? extends ProcessHandler> pClass) {
        super(String.format("%s is not register or process handler is null", pClass.getName()));
    }
}
