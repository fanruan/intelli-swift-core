package com.fr.swift.transaction;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface TransactionManager<T> {

    void setOldAttach(T t);

    void start();

    void commit();

    void rollback();

    void close();
}
