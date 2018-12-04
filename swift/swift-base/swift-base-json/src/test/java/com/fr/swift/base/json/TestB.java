package com.fr.swift.base.json;

import com.fr.swift.base.json.annotation.JsonProperty;

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
        return "com.fr.swift.base.json.TestB{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
