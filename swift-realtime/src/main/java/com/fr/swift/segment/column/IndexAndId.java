package com.fr.swift.segment.column;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.io.impl.mem.IntMemIo;
import com.fr.swift.util.Crasher;

/**
 * @author anchore
 * @date 2018/8/15
 */
class IndexAndId implements Cloneable, Releasable {
    private IntMemIo indexToId = new IntMemIo();

    private IntMemIo idToIndex = new IntMemIo();

    private int lastPos = -1;

    void putIndexAndId(int id, int index) {
        idToIndex.put(id, index);
        indexToId.put(index, id);

        int max = Math.max(id, index);
        if (lastPos < max) {
            lastPos = max;
        }
    }

    int getId(int index) {
        return indexToId.get(index);
    }

    int getIndex(int id) {
        return idToIndex.get(id);
    }

    int size() {
        return lastPos + 1;
    }

    @Override
    protected final IndexAndId clone() {
        IndexAndId cloned;
        try {
            cloned = (IndexAndId) super.clone();
            cloned.indexToId = indexToId.clone();
            cloned.idToIndex = idToIndex.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    public void release() {
        indexToId.release();
        idToIndex.release();
    }
}