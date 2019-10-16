package com.fr.swift.basic;

/**
 * This class created on 2019/3/13
 *
 * @author Lucifer
 * @description
 */
public interface Response {

    boolean hasException();

    String getRequestId();

    Throwable getException();

    Object getResult();

    boolean isError();
}
