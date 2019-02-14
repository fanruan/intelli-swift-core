package com.fr.swift.config.dao;

import com.fr.swift.converter.ObjectConverter;

/**
 * @author yee
 * @date 2018-11-28
 */
public class TestBean implements ObjectConverter<TestEntity> {
    @Override
    public TestEntity convert() {
        return new TestEntity();
    }
}
