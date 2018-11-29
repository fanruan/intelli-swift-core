package com.fr.swift.transaction;

/**
 * This class created on 2018/6/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TransactionTestBean implements ITransactionTestBean {

    @Transactional
    public int add(int a, int b) {
        return a + b;
    }

    @Transactional
    public int divided(int a) {
        return a / 0;
    }
}
