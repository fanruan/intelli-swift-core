package com.fr.swift.basics;

import com.fr.swift.basics.annotation.Target;

import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/23
 */
public interface ProcessHandler {

    /**
     * process result
     * 只负责远程调用的逻辑和结果合并，不负责url的计算
     *
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    Object processResult(Method method, Target[] targets, Object... args) throws Throwable;
}
