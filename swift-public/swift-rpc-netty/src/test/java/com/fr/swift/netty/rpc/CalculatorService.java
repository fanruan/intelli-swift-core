package com.fr.swift.netty.rpc;

/**
 * This class created on 2018/6/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface CalculatorService {

    int add(int a, int b, long sleepTime);

    int multiply(int a, int b, long sleepTime);
}
