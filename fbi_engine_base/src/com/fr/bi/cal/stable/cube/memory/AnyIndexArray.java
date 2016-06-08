package com.fr.bi.cal.stable.cube.memory;

/**
 * Created by 小灰灰 on 2016/5/19.
 */
public class AnyIndexArray<E>  {
    private Object[] list;

    private static final int CAPACITY = 8;

    private int size;

    private NullChecker<E> checker;

    public AnyIndexArray(NullChecker<E> checker) {
        this.list = new Object[CAPACITY];
        size = 0;
        this.checker = checker;
    }

    public int size() {
        return size;
    }

    public E get(int index) {
        if (index >= size || index < 0){
            throw new ArrayIndexOutOfBoundsException(index);
        }
        E e =  (E) list[index];
        return checker.isNull(e) ? null : e;
    }

    public void add(int index, E element) {
        if (index < 0){
            throw new ArrayIndexOutOfBoundsException(index);
        }
        checkIndex(index);
        element = checker.isNull(element) ? null : element;
        list[index] = element;
        size = Math.max(size, index + 1);
    }

    private void checkIndex(int index) {
        if (list.length <= index){
            int capacity = list.length;
            while (capacity <= index){
                capacity = capacity << 1;
            }
            Object[] obs = new Object[capacity];
            System.arraycopy(list, 0, obs, 0, size);
            list = obs;
        }
    }

}
