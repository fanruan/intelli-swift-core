package com.fr.swift.test;

import org.junit.Before;

/**
 * @author anchore
 * @date 2018/5/8
 */
public abstract class TestIo {
    @Before
    public void before() {
        Preparer.prepareCubeBuild(getClass());
    }
}