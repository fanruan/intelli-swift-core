package com.fr.swift.config.convert.base;

import com.fr.swift.config.annotation.ConfigField;

/**
 * @author yee
 * @date 2018-11-29
 */
public class TestObject {
    @ConfigField
    private String name;
    @ConfigField
    private int age;

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
}
