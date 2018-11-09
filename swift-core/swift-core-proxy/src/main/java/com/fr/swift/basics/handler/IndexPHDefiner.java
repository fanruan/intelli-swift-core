package com.fr.swift.basics.handler;


import com.fr.swift.basics.ProcessHandler;

/**
 * @author yee
 * @date 2018/10/25
 */
public interface IndexPHDefiner<T> {
    interface IndexProcessHandler<T> extends ProcessHandler<T> {
    }

    interface StatusProcessHandler<T> extends ProcessHandler<T> {
    }
}
