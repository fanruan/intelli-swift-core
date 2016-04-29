package com.fr.bi.stable.structure.collection.list;

import com.fr.general.ComparatorUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Connery on 2015/12/3.
 */
public abstract class BIArrayList<T> implements Cloneable, Serializable, BIList<T> {
    /**
     *
     */
    private static final long serialVersionUID = -4418095160093972796L;
    private T[] data; // An array to store the numbers.
    private int size = 0;  // Index of next unused element of array

    /**
     * 默认的构造函数，生成一个容量为8的整数链表
     */
    public BIArrayList() {
        this(8);
    }

    /**
     * 生成一个指定容量的整数链表
     *
     * @param initialCapacity 容量
     */
    public BIArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " +
                    initialCapacity);
        }
        data = generateEmptyArray(initialCapacity);
    }

    /*
     * test subList
     */
    public static void main(String[] args) {
        IntList list = new IntList();
        for (int i = 0; i < 100; i++) {
            list.add(1000 - i);
        }
        list.subList(10);
//        System.out.println(list);
    }

//    /**
//     * 将一个整数数组转化成一个整数链表
//     *
//     * @param array 要转化的整数数组
//     * @return 整数数组转化成的整数链表
//     */
//    public static IntList asList(T[] array) {
//        if (array == null) {
//            throw new NullPointerException();
//        }
//
//        IntList list = new IntList();
//        list.data = array;
//        list.size = list.data.length;
//        return list;
//    }
//
//    /**
//     * 给指定的整数列表排序
//     *
//     * @param list 待排序的整数列表
//     */
//    public static void sort(IntList list) {
//        int[] array = list.toArray();
//        Arrays.sort(array);
//        list.data = array;
//    }

    protected abstract T[] generateEmptyArray(Integer capacity);

    @Override
	public T getLastValue() {
        return data[size - 1];
    }

    @Override
	public void setLastValue(T v) {
        data[size - 1] = v;
    }

    /**
     * 获取整数链表指定位置的元素
     *
     * @param i 要获取元素的索引
     * @return 链表元素
     * @throws ArrayIndexOutOfBoundsException 指定的位置比整数链表容量大时抛出此异常
     */
    @Override
	public T get(Integer index) throws ArrayIndexOutOfBoundsException {

        if (!isLegalBoundary(index)) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else {
            return data[index];
        }
    }

    private Boolean isLegalBoundary(Integer index) {
        return index < size && index >= 0;
    }

    /**
     * 设置整数链表指定位置的元素为给定值
     *
     * @param index 指定的索引
     * @param value 要设置的值
     * @throws ArrayIndexOutOfBoundsException 指定的索引超过链表容量时抛出此异常
     */
    @Override
	public void set(Integer index, T value) throws ArrayIndexOutOfBoundsException {
        if (!isLegalBoundary(index)) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else {
            data[index] = value;
        }
    }

    /**
     * 添加一个元素到链表中，在必要的时候链表的容量会自动增加
     *
     * @param x 要添加到链表中的元素
     */
    @Override
	public void add(T x) {
        ensureCapacity(size + 1);
        data[size++] = x;         // Store the int in it.
    }

    /**
     * 在链表的指定位置插入一个元素
     *
     * @param index 要插入元素的位置
     * @param x     要插入的元素
     */
    @Override
	public void add(Integer index, T x) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1); // Increments modCount!!
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = x;
        size++;
    }

    /**
     * 判断指定值是否包含在此整数链表中
     *
     * @param value 指定的值
     * @return 如果链表中包含此值则返回true，否则返回false
     */
    @Override
	public boolean contains(T value) {
        return indexOf(value) >= 0;
    }

    /**
     * 获取指定值在链表中的位置，如果该值在链表中不存在则返回-1.
     *
     * @param value 指定的值
     * @return 值在链表中的位置
     */
    @Override
	public int indexOf(T value) {
        for (int i = 0; i < size; i++) {
            if (ComparatorUtils.equals(data[i], value)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 获取链表的元素的个数
     *
     * @return 链表元素的个数
     */
    @Override
	public int size() {
        return size;
    }

    /**
     * 移除链表中指定位置的元素
     *
     * @param index 要移除的元素在链表中的索引
     */
    @Override
	public void remove(Integer index) {
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(data, index + 1, data, index, numMoved);
        }
        --size;
    }

    @Override
	public void subList(int length) {
        if (size > length) {
            System.arraycopy(data, 0, data, 0, length);
            size = length;
        }
    }

    /**
     * 移除整数链表中所有的相等等数字
     */
    @Override
	public void removeEqual() {
        T[] newDataArray = this.toArray();

        this.clear();
        for (int i = 0; i < newDataArray.length; i++) {
            if (!this.contains(newDataArray[i])) {
                this.add(newDataArray[i]);
            }
        }
    }

    /**
     * 排序
     */
    @Override
	public void sort() {
        this.trimToSize();
        Arrays.sort(this.data);
    }

    /**
     * 清楚整数链表中的所有元素
     */
    @Override
	public void clear() {
        this.data = generateEmptyArray(0);
        this.size = 0;
    }

    /**
     * Trim to Size.
     */
    @Override
	public void trimToSize() {
        this.data = this.toArray();
    }

    /**
     * 将链表转化成整数数组
     *
     * @return 整数数组
     */
    @Override
	public T[] toArray() {
        T[] array = generateEmptyArray(size);

        System.arraycopy(data, 0, array, 0, size);

        return array;
    }

    /**
     * 将指定的新的整数链表中的所有元素插入到当前链表中指定位置
     *
     * @param index 要插入的位置
     * @param c     要插入的整数链表
     * @return 如果在当前链表中成功插入了元素，则返回true，否则返回false
     */
    @Override
	public boolean addAll(Integer index, BIList c) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + size);
        }
        Object[] a = c.toArray();

        int numNew = a.length;
        ensureCapacity(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0) {
            System.arraycopy(data, index, data, index + numNew,
                    numMoved);
        }
        System.arraycopy(a, 0, data, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * 将指定的整数列表的所有元素添加到当前列表中
     *
     * @param c 要添加的整数列表
     * @return 如果在当前列表添加了元素则返回true，否则返回false
     */
    @Override
	public boolean addAll(BIList c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacity(size + numNew);  // Increments modCount
        System.arraycopy(a, 0, data, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * 判断当前对象是否和指定的对象相等
     *
     * @return 相等则返回true，不相等则返回false
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntList)) {
            return false;
        }

        BIArrayList that = (BIArrayList) o;
        if (this.size != that.size) {
            return false;
        }

        for (int i = 0; i < this.size; i++) {
            if (this.data[i] != that.data[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return 克隆的对象
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        BIList newIntList = (BIList) super.clone();
        newIntList.clear();

        for (int i = 0; i < this.size(); i++) {
            newIntList.add(this.get(i));
        }

        return newIntList;
    }

    /**
     * 表示该整数链表对象的字符串
     *
     * @return 链表对象的字符串表示
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0, len = size(); i < len; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(get(i));
        }
        sb.append(']');

        return sb.toString();
    }

    /**
     * An internal method to change the allocated size of the array
     */
    private void ensureCapacity(int minCapacity) {
        int oldCapacity = this.data.length;
        if (minCapacity > oldCapacity) {
            T[] oldData = data;
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            data = generateEmptyArray(newCapacity);
            System.arraycopy(oldData, 0, data, 0, size);
        }
    }

    @Override
    public int hashCode() {
        int result = data != null ? Arrays.hashCode(data) : 0;
        result = 31 * result + size;
        return result;
    }
}