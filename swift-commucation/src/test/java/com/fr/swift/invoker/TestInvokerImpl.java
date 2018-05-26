package com.fr.swift.invoker;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TestInvokerImpl implements TestInvokerInterface {
    private String id;
    private String name;
    private int age;

    public TestInvokerImpl(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String print(long time) {
        System.out.println("time:" + time);
        System.out.println("id:" + id);
        System.out.println("name:" + name);
        System.out.println("age:" + age);
        return time + id + name + age;
    }
}
