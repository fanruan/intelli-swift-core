package com.fr.swift.beans.factory.recursion.bean.singleton;

import com.fr.swift.beans.annotation.SwiftBean;

/**
 * This class created on 2018/11/28
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@SwiftBean
public class RecursionD {
    public RecursionC recursionC = TestBeanFactory.getInstance().getBean(RecursionC.class);
    public RecursionB recursionB = TestBeanFactory.getInstance().getBean(RecursionB.class);

}
