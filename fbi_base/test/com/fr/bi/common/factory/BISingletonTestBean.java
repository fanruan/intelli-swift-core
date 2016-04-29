package com.fr.bi.common.factory;

import com.fr.bi.common.factory.annotation.BISingletonObject;

/**
 * Created by Connery on 2015/12/7.
 */
@BISingletonObject
public class BISingletonTestBean {
    public String name;

    public BISingletonTestBean(String name) {
        this.name = name;
    }
}