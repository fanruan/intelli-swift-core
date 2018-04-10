package com.fr.swift.generate.history.index;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.impl.RoaringMutableBitMap;
import com.fr.swift.cube.nio.NIOConstant;
import com.fr.swift.structure.array.LongArray;
import com.fr.swift.structure.array.LongListFactory;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/30
 */
public class RelationIndexHelper {
    private List<LongArray[]> index;
    private List<LongArray> revert;
    private List<LongArray> nullIndex;

    public RelationIndexHelper() {
        this.index = new ArrayList<LongArray[]>();
        this.revert = new ArrayList<LongArray>();
        this.nullIndex = new ArrayList<LongArray>();
    }

    public void addIndex(byte[][] index) {
        int length = index.length;
        ImmutableBitMap[] target = new ImmutableBitMap[length];
        for (int i = 0; i < length; i++) {
            target[i] = RoaringMutableBitMap.fromBytes(index[i]);
        }
//        this.index.add(target);
    }

    public void addIndex(LongArray[] index) {
        this.index.add(index);
    }

    public void addRevert(LongArray revert) {
        this.revert.add(revert);
    }

    public void addNullIndex(LongArray nullIndex) {
        this.nullIndex.add(nullIndex);
    }

    public LongArray[] getIndex() {
        if (index.isEmpty()) {
            Crasher.crash("index is empty");
        }
        LongArray[] result = index.get(0);
        for (int i = 1, size = index.size(); i < size; i++) {
            LongArray[] tmp = index.get(i);
            for (int j = 0, len = result.length; j < len; j++) {
                LongArray targetValue = result[j];
                LongArray arrayValue = tmp[j];
                result[j] = match(arrayValue, targetValue);
            }
        }
        return result;
    }

    private LongArray match(LongArray array, LongArray target) {
        List<Long> list = new ArrayList<Long>();
        for (int i = 0; i < target.size(); i++) {
            long targetValue = target.get(i);
            for (int j = 0; j < array.size(); j++) {
                long arrayValue = array.get(j);
                if (targetValue == arrayValue && targetValue != NIOConstant.LONG.NULL_VALUE && !list.contains(targetValue)) {
                    list.add(targetValue);
                }
            }
        }
        return LongListFactory.fromList(list);
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

    public LongArray getNullIndex() {
        List<Long> longList = new ArrayList<Long>();
        for (int i = 0, size = nullIndex.size(); i < size; i++) {
            LongArray array = nullIndex.get(i);
            for (int j = 0; j < array.size(); j++) {
                Long value = array.get(j);
                if (!longList.contains(value)) {
                    longList.add(value);
                }
            }
        }
        return LongListFactory.fromList(longList);
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
