package com.fr.swift.base.json;

import com.fr.swift.base.json.annotation.JsonProperty;

/**
 * @author yee
 * @date 2018-12-04
 */
public class TestC extends BaseType {
    @JsonProperty("testInf")
    private TestInf testInf;

    public TestC(TestInf testInf) {
        super(Type.C);
        this.testInf = testInf;
    }

    public TestC() {
        super(Type.C);
    }

    public TestInf getTestInf() {
        return testInf;
    }
}
