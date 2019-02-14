package com.fr.swift.config.dao;

import com.fr.swift.converter.ObjectConverter;

/**
 * @author yee
 * @date 2018-11-28
 */
public class TestEntity implements ObjectConverter<TestBean> {
    @Override
    public TestBean convert() {
        return new TestBean();
    }
}
