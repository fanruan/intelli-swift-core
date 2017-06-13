package com.fr.bi.cal.stable.cube.memory;

import com.fr.bi.exception.BIMemoryDataOutOfLimitException;

/**
 * Created by 小灰灰 on 2017/6/9.
 */
public class DataLimitAnyIndexArray<E> extends AnyIndexArray<E> {
    private int limit;

    public DataLimitAnyIndexArray(int limit) {
        super();
        this.limit = limit;
    }

    @Override
    public void add(int index, E element) {
        if (index >= limit){
            throw new BIMemoryDataOutOfLimitException();
        }
        super.add(index, element);
    }
}
