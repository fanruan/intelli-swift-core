package com.fr.swift.beans.factory.recursion.bean.prototype;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.beans.annotation.SwiftScope;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
@SwiftScope(value = "prototype")
public class RecursionC {
    public RecursionA recursionA = TestBeanFactory.getInstance().getBean(RecursionA.class);
}
