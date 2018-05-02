package com.fr.swift.query.group.by2;

import java.util.Iterator;

/**
 * Created by Lyon on 2018/4/23.
 */
interface IteratorCreator<ENTRY> {

    /**
     * 创建树迭代器的子迭代器
     *
     * @param stackSize 树迭代器当前栈的大小
     * @param entry     创建当前迭代器依赖的元素
     * @return
     */
    Iterator<ENTRY> createIterator(int stackSize, ENTRY entry);
}
