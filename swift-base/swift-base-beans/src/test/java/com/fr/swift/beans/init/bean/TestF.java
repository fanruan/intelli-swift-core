package com.fr.swift.beans.init.bean;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;

@SwiftBean(name = "F")
public class TestF {
    @SwiftAutoWired
    public TestD d;
}
