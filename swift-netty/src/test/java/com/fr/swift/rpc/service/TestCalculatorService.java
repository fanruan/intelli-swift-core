package com.fr.swift.rpc.service;

import com.fr.swift.rpc.CalculatorService;
import com.fr.swift.rpc.annotation.RpcMethod;
import com.fr.swift.rpc.annotation.RpcService;
import com.fr.swift.rpc.annotation.RpcServiceType;

/**
 * This class created on 2018/6/11
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RpcService(value = CalculatorService.class, type = RpcServiceType.SERVER_SERVICE)
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
