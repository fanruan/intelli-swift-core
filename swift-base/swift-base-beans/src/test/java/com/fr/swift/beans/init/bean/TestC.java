package com.fr.swift.beans.init.bean;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;

@SwiftBean(name = "C")
public class TestC extends TestB {

    @SwiftAutoWired
    public TestF f = new TestF();

    public TestF getF() {
        return f;
    }

    TestF getSF() {
        return super.f;
    }

}
