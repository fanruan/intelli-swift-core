package com.fr.swift.util.function;

/**
 * Represents a predicate (boolean-valued function) of one argument.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #test(Object)}.
 * <p>
 * 谓词，用于判断
 *
 * @param <E> the type of the input to the predicate
 * @author anchore
 * @date 2018/4/9
 */
public interface Predicate<E> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param e the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean test(E e);
}