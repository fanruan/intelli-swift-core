package com.fr.swift.basics;

import com.fr.swift.basics.annotation.Target;

import java.lang.reflect.Method;

/**
 * @author yee
 * @date 2018/10/23
 */
public interface ProcessHandler<T> {
    String TO_STRING = "toString";
    String HASH_CODE = "hashCode";
    String EQUALS = "equals";

    /**
     * process result
     * 只负责远程调用的逻辑和结果合并，不负责url的计算
     *
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    Object processResult(Method method, Target target, Object... args) throws Throwable;

    /**
     * process target url
     * 只负责各种形式的url计算。
     *
     * @return
     */
    T processUrl(Target target, Object... args);
}
