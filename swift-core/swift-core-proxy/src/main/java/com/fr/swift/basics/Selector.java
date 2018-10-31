package com.fr.swift.basics;

/**
 * This class created on 2018/5/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface Selector<T> {

    T getFactory();

    void switchFactory(T factory);
}
