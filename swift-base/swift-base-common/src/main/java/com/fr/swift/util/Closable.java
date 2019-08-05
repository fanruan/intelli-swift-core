package com.fr.swift.util;

/**
 * @author anchore
 * @date 2018/12/19
 * Closable的子类也可以愉快的写try-with-resource了，暂时作为过渡的接口
 */
public interface Closable extends AutoCloseable {
}