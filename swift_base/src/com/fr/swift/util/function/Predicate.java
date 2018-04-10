package com.fr.swift.util.function;

/**
 * @author anchore
 * @date 2018/4/9
 * <p>
 * 谓词，用于判断
 */
public interface Predicate<E> {
    boolean test(E e);
}