package com.fr.swift.beans.init.bean;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;


@SwiftBean(name = "A")
public class TestA {
    private int a = 7;
    @SwiftAutoWired
    public SuperE e;
    @SwiftAutoWired
    public TestF f;
    @SwiftAutoWired
    public TestD d;
}
