package com.fr.swift.basics;

/**
 * This class created on 2018/6/12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface AsyncRpcCallback {

    void success(Object result);

    void fail(Exception e);

}
