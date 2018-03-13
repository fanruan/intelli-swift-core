package com.fr.swift.structure.stack;

/**
 * Created by Lyon on 2018/3/1.
 *
 * todo 其实java.util.Deque就满足lifo，不用再写个接口
 */
public interface Stack<T> {

    /**
     * 是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 栈允许的最大值
     * @return
     */
    int limit();

    /**
     * 栈里面的元素个数
     * @return
     */
    int size();

    /**
     * 添加
     * @param item
     */
    void push(T item);

    /**
     * 返回并删除栈顶元素
     * @return
     */
    T pop();

    /**
     * 返回栈顶元素
     * @return
     */
    T peek();
}
