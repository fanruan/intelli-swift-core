package com.fr.swift.basics;

import com.fr.swift.basics.annotation.Target;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yee
 * @date 2018/10/23
 */
public interface ProcessHandler {
    String TO_STRING = "toString";
    String HASH_CODE = "hashCode";
    String EQUALS = "equals";

    /**
     * process result
     *
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    Object processResult(Method method, Target target, Object... args) throws Throwable;

    /**
     * process target url
     *
     * @return
     */
    List<URL> processUrl(Target target);
}
