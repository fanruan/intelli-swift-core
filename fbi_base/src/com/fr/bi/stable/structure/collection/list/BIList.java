package com.fr.bi.stable.structure.collection.list;


/**
 * Created by Connery on 2015/12/2.
 */
public interface BIList<T> {

    public T get(Integer row) throws ArrayIndexOutOfBoundsException;

    void set(Integer index, T value) throws ArrayIndexOutOfBoundsException;

    void add(T x);

    void add(Integer index, T x);

    int size();

    T getLastValue();

    void setLastValue(T v);

    boolean contains(T value);

    /**
     * 获取指定值在链表中的位置，如果该值在链表中不存在则返回-1.
     *
     * @param value 指定的值
     * @return 值在链表中的位置
     */
    int indexOf(T value);

    /**
     * 移除链表中指定位置的元素
     *
     * @param index 要移除的元素在链表中的索引
     */
    void remove(Integer index);

    void subList(int length);

    void removeEqual();

    T[] toArray();

    /**
     * 排序
     */
    void sort();

    /**
     * 清楚整数链表中的所有元素
     */
    void clear();

    void trimToSize();

    boolean addAll(Integer index, BIList c);

    boolean addAll(BIList c);

}