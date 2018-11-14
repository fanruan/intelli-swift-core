package com.fr.swift.invoker;

import com.fr.swift.basics.annotation.ProxyService;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@ProxyService(TestInvokerInterface.class)
public class TestInvokerImpl implements TestInvokerInterface {
    private String id;
    private String name;
    private int age;

    public TestInvokerImpl() {
    }

    public String print(String id, String name, int age, long time) {
        this.id = id;
        this.name = name;
        this.age = age;
        System.out.println("time:" + time);
        System.out.println("id:" + id);
        System.out.println("name:" + name);
        System.out.println("age:" + age);
        return id + name + age + time;
    }
}
