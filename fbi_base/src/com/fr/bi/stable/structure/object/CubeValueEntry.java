package com.fr.bi.stable.structure.object;

import com.fr.bi.stable.gvi.GroupValueIndex;

/**
 * Created by loy on 16/6/20.
 */
public class CubeValueEntry<T> {

    private T t;
    private GroupValueIndex gvi;
    private int index;

    public CubeValueEntry(T t, GroupValueIndex gvi, int index) {
        this.t = t;
        this.gvi = gvi;
        this.index = index;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public GroupValueIndex getGvi() {
        return gvi;
    }

    public void setGvi(GroupValueIndex gvi) {
        this.gvi = gvi;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
