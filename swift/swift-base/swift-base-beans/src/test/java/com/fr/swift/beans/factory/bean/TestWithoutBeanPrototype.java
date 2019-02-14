package com.fr.swift.beans.factory.bean;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;

/**
 * This class created on 2018/11/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean(name = "test_custom")
@SwiftScope(value = "prototype")
public class TestWithoutBeanPrototype implements ITestWithoutBeanPrototype {

    public void print() {

    }
}
