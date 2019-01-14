package com.fr.swift.jdbc.rpc.serializable;

import java.io.Serializable;

/**
 * @author yee
 * @date 2019-01-11
 */
public class TestBean implements Serializable {
    private static final long serialVersionUID = -7365646813614144380L;

    private String name;
    private int age;

    public TestBean() {
    }

    public TestBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestBean testBean = (TestBean) o;

        if (age != testBean.age) return false;
        return name != null ? name.equals(testBean.name) : testBean.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        return result;
    }
}
