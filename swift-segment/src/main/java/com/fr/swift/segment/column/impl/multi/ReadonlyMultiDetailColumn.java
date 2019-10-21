package com.fr.swift.segment.column.impl.multi;

import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.util.IoUtil;

import java.util.List;

/**
 * @author anchore
 * @date 2019/7/11
 */
class ReadonlyMultiDetailColumn<T> implements DetailColumn<T> {
    private List<DetailColumn<T>> details;

    private int[] offsets;

    public ReadonlyMultiDetailColumn(List<DetailColumn<T>> details, int[] offsets) {
        this.details = details;
        this.offsets = offsets;
    }

    @Override
    public T get(int pos) {
        for (int i = 0; i < offsets.length - 1; i++) {
            if (pos >= offsets[i] && pos < offsets[i + 1]) {
                return details.get(i).get(pos - offsets[i]);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public void release() {
        IoUtil.release(details.toArray(new DetailColumn[0]));
    }

    @Override
    public int getInt(int pos) {
        for (int i = 0; i < offsets.length - 1; i++) {
            if (pos >= offsets[i] && pos < offsets[i + 1]) {
                return details.get(i).getInt(pos - offsets[i]);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public long getLong(int pos) {
        for (int i = 0; i < offsets.length - 1; i++) {
            if (pos >= offsets[i] && pos < offsets[i + 1]) {
                return details.get(i).getLong(pos - offsets[i]);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public double getDouble(int pos) {
        for (int i = 0; i < offsets.length - 1; i++) {
            if (pos >= offsets[i] && pos < offsets[i + 1]) {
                return details.get(i).getDouble(pos - offsets[i]);
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void put(int pos, T val) {
        throw new UnsupportedOperationException();
    }
}