package com.fr.swift.netty.rpc.service;

import com.fr.swift.annotation.RpcMethod;
import com.fr.swift.annotation.RpcService;
import com.fr.swift.annotation.RpcServiceType;
import com.fr.swift.netty.rpc.CalculatorService;

/**
 * This class created on 2018/6/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RpcService(value = CalculatorService.class, type = RpcServiceType.INTERNAL)
public class TestCalculatorService implements CalculatorService {

    @Override
    @RpcMethod(methodName = "add")
    public int add(int a, int b, long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
        return a + b;
    }

    @Override
    @RpcMethod(methodName = "multiply")
    public int multiply(int a, int b, long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
        return a * b;
    }
}
