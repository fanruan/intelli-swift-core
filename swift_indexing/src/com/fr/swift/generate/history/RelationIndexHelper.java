package com.fr.swift.generate.history;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.structure.array.IntArray;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/30
 */
public class RelationIndexHelper {
    private List<ImmutableBitMap[]> index;
    private List<IntArray> revert;
    private List<ImmutableBitMap> nullIndex;

    public RelationIndexHelper() {
        this.index = new ArrayList<ImmutableBitMap[]>();
        this.revert = new ArrayList<IntArray>();
        this.nullIndex = new ArrayList<ImmutableBitMap>();
    }

    public void addIndex(byte[][] index) {
        int length = index.length;
        ImmutableBitMap[] target = new ImmutableBitMap[length];
        for (int i = 0; i < length; i++) {
            target[i] = RoaringMutableBitMap.fromBytes(index[i]);
        }
        this.index.add(target);
    }

    public void addRevert(IntArray revert) {
        this.revert.add(revert);
    }

    public void addNullIndex(ImmutableBitMap nullIndex) {
        this.nullIndex.add(nullIndex);
    }

    public ImmutableBitMap[] getIndex() {
        if (index.isEmpty()) {
            Crasher.crash("index is empty");
        }
        ImmutableBitMap[] result = index.get(0);
        for (int i = 1, size = index.size(); i < size; i++) {
            ImmutableBitMap[] tmp = index.get(i);
            for (int j = 0, len = result.length; j < len; j++) {
                result[j] = result[j].getAnd(tmp[j]);
            }
        }
        return result;
    }

    public IntArray getRevert() {
        if (revert.isEmpty()) {
            Crasher.crash("revert array is empty");
        }
        IntArray target = revert.get(0);
        for (int i = 1, size = revert.size(); i < size; i++) {
            IntArray array = revert.get(i);
            for (int j = 0, len = target.size(); j < len; j++) {
                target.put(j, target.get(j) & array.get(j));
            }
        }
        return target;
    }

    public ImmutableBitMap getNullIndex() {
        ImmutableBitMap result = null;
        for (int i = 0, size = nullIndex.size(); i < size; i++) {
            if (null == result) {
                result = nullIndex.get(i);
            } else {
                result = result.getOr(nullIndex.get(i));
            }
        }
        return result;
    }
}
