package com.fr.bi.cal.stable.cube.memory;

/**
 * Created by 小灰灰 on 2016/5/19.
 */
public class AnyIndexArray<E>  {
    private Object[] list;

    private static final int CAPACITY = 8;

    private int size;

    public AnyIndexArray() {
        this.list = new Object[CAPACITY];
        size = 0;
    }

    public int size() {
        return size;
    }

    public E get(int index) {
        if (index >= size || index < 0){
            throw new ArrayIndexOutOfBoundsException(index);
        }
        return (E) list[index];
    }

    public void add(int index, E element) {
        if (index < 0){
            throw new ArrayIndexOutOfBoundsException(index);
        }
        checkIndex(index);
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
