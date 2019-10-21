package com.fr.swift.local;

/**
 * This class created on 2018/11/16
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ITestInvokerImpl implements ITestInvoker {
    @Override
    public String print(String id, String name, int age, long time) {
        return id + name + age + time;
    }
}
