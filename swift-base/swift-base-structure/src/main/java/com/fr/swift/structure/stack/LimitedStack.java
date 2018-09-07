package com.fr.swift.structure.stack;

import java.util.List;

/**
 * Created by Lyon on 2018/3/1.
 */
public interface LimitedStack<T> {

    /**
     * 是否为空
     *
     * @return
     */
    boolean isEmpty();

    /**
     * 栈允许的最大值
     * 类库提供的java.util.Deque实现都是自动无限增长
     *
     * @return
     */
    int limit();

    /**
     * 栈里面的元素个数
     *
     * @return
     */
    int size();

    /**
     * 添加
     *
     * @param item
     */
    void push(T item);

    /**
     * 返回并删除栈顶元素
     *
     * @return
     */
    T pop();

    /**
     * 返回栈顶元素
     *
     * @return
     */
    T peek();

    /**
     * 长度为limit，空值用null填充的list，浅拷贝
     *
     * @return
     */
    List<T> toList();
}
