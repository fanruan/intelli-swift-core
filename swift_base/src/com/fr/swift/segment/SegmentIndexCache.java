package com.fr.swift.segment;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.MutableBitMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SegmentIndexCache {

    private Map<Integer, Map<String, MutableBitMap>> nullMap = new ConcurrentHashMap<Integer, Map<String, MutableBitMap>>();
    private Map<Integer, Integer> segIndexRowMap = new HashMap<Integer, Integer>();
    private Map<Integer, Segment> SegmentIndexMap = new HashMap<Integer, Segment>();


    public void putSegRow(int index, int row) {
        segIndexRowMap.put(index, row);
    }

    public int getSegRowByIndex(int index) {
        return segIndexRowMap.get(index);
    }

    public void putSegment(int index, Segment segment) {
        if (!SegmentIndexMap.containsKey(index)) {
            SegmentIndexMap.put(index, segment);
        }
    }

    public Segment getSegmentByIndex(int index) {
        return SegmentIndexMap.get(index);
    }

    public void putSegFieldNull(int index, String field, int rowIndex) {
        if (nullMap.containsKey(index)) {
            Map<String, MutableBitMap> fieldNullMap = nullMap.get(index);
            if (fieldNullMap.containsKey(field)) {
                MutableBitMap nullIndex = fieldNullMap.get(field);
                nullIndex.add(rowIndex);
            } else {
                MutableBitMap nullIndex = BitMaps.newRoaringMutable();
                nullIndex.add(rowIndex);
                fieldNullMap.put(field, nullIndex);
            }
        } else {
            nullMap.put(index, new HashMap<String, MutableBitMap>());
            MutableBitMap nullIndex = BitMaps.newRoaringMutable();
            nullIndex.add(rowIndex);
            nullMap.get(index).put(field, nullIndex);
        }
    }

    public Map<Integer, Segment> getNewSegMap() {
        return this.SegmentIndexMap;
    }

    public MutableBitMap getNullBySegAndField(int index, String field) {
        try {
            if (nullMap.get(index).get(field) != null) {
                return nullMap.get(index).get(field);
            } else {
                return BitMaps.newRoaringMutable();
            }
        } catch (NullPointerException npe) {
            return BitMaps.newRoaringMutable();
        }
    }

    public int getNewSegSize() {
        return SegmentIndexMap.size();
    }
}