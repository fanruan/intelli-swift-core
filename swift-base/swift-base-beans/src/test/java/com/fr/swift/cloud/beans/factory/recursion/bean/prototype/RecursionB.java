package com.fr.swift.cloud.beans.factory.recursion.bean.prototype;

import com.fr.swift.cloud.beans.annotation.SwiftBean;
import com.fr.swift.cloud.beans.annotation.SwiftScope;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
@SwiftScope(value = "prototype")
public class RecursionB {
    public RecursionA recursionA = TestBeanFactory.getInstance().getBean(RecursionA.class);
}
