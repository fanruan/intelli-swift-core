package com.fr.swift.beans.init.beancreator;

import com.fr.swift.beans.annotation.SwiftAutoWired;
import com.fr.swift.beans.annotation.SwiftBean;

@SwiftBean(name = "F")
public class TestF {
    @SwiftAutoWired
    public TestD d;
}
