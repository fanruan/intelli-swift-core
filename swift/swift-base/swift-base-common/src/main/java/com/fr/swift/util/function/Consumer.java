package com.fr.swift.util.function;

/**
 * @author anchore
 * @date 2018/4/9
 * <p>
 * 消费者
 */
public interface Consumer<E> {
    void accept(E e);
}