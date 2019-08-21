package com.fr.swift.beans.init.beancreator;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;

@SwiftBean(name = "B")
public class TestB extends TestA {

    @SwiftAutoWired
    public TestF f;

    public void print() {
        System.out.println("I'm B");
    }
}
