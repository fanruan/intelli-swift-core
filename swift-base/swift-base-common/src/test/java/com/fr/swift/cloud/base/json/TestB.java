package com.fr.swift.cloud.base.json;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-04
 */
public class TestB extends BaseType {
    @JsonProperty("name")
    private String name;
    @JsonProperty("age")
    private int age;

    public TestB() {
        super(Type.B);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestB{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
