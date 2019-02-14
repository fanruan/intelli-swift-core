package com.fr.swift.generate.history.index;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/30
 */
public class RelationIndexHelper {
    private List<MutableBitMap[]> index;
    private List<LongArray> revert;
    private List<ImmutableBitMap> nullIndex;

    public RelationIndexHelper() {
        this.index = new ArrayList<MutableBitMap[]>();
        this.revert = new ArrayList<LongArray>();
        this.nullIndex = new ArrayList<ImmutableBitMap>();
    }

    public void addIndex(byte[][] index) {
        int length = index.length;
        MutableBitMap[] target = new MutableBitMap[length];
        for (int i = 0; i < length; i++) {
            target[i] = RoaringMutableBitMap.ofBytes(index[i]);
        }
        this.index.add(target);
    }

    public void addIndex(MutableBitMap[] index) {
        this.index.add(index);
    }

    public void addRevert(LongArray revert) {
        this.revert.add(revert);
    }

    public void addNullIndex(ImmutableBitMap nullIndex) {
        this.nullIndex.add(nullIndex);
    }

    public ImmutableBitMap[] getIndex() {
        if (index.isEmpty()) {
            Crasher.crash("index is empty");
        }
        MutableBitMap[] result = index.get(0);
        for (int i = 1, size = index.size(); i < size; i++) {
            ImmutableBitMap[] tmp = index.get(i);
            for (int j = 0, len = result.length; j < len; j++) {
                result[j].and(tmp[j]);
            }
        }
        return result;
    }

    public LongArray getRevert() {
        if (revert.isEmpty()) {
            Crasher.crash("revert array is empty");
        }
        LongArray target = revert.get(0);
        for (int i = 1, size = revert.size(); i < size; i++) {
            LongArray array = revert.get(i);
            for (int j = 0, len = target.size(); j < len; j++) {
                long targetValue = target.get(j);
                long arrayValue = array.get(j);
                target.put(j, targetValue == arrayValue ? targetValue : NIOConstant.LONG.NULL_VALUE);
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

    /**
     * 将块号和行号封装成long
     * @param seg 块号
     * @param row 行号
     * @return
     */
    public static long merge2Long(int seg, int row) {
        return ((long) row & 0xFFFFFFFFL) | (((long) seg << 32) & 0xFFFFFFFF00000000L);
    }

    /**
     * 根据long来获取segment序号和segment行号
     * @param reverse
     * @return
     */
    public static int[] reverse2SegAndRow(long reverse) {
        int[] result = new int[2];
        result[0] = (int) ((reverse & 0xFFFFFFFF00000000L) >> 32);
        result[1] = (int) (0xFFFFFFFFL & reverse);
        return result;
    }
}
