package com.fr.bi.common.factory;

/**
 * Created by Connery on 2015/12/7.
 */
public class BITestBean {
    public String name;
    public long age;
    public Long objAge;

    public BITestBean(String name) {
        this.name = name;
    }

    public BITestBean(long age) {
        this.age = age;
    }

    public BITestBean(Long age) {
        this.objAge = age;
    }

    public BITestBean(Long age, long a) {
        this.objAge = age;
        this.age = a;
    }

    public BITestBean() {
        this.age = -2;
    }
}