package com.fr.swift.exception;

/**
 * @author anchore
 * @date 2019/8/15
 * <p>
 * 异常上下文
 * <p>
 * 粒度最好细到单个元素，好做id
 */
public interface ExceptionContext {
    ExceptionContext clone();
}