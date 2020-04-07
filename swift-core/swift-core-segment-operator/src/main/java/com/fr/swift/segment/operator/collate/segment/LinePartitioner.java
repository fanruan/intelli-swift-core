package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.traversal.BreakTraversalAction;
import com.fr.swift.segment.Segment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
public class LinePartitioner implements Partitioner {

    private int capacity;

    public LinePartitioner(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public List<SegmentItem> partition(List<Segment> segments) {
        // 先排序 从低到高
        segments.sort(Comparator.comparingInt(o -> o.getAllShowIndex().getCardinality()));
        List<ImmutableBitMap> allShows = new ArrayList<ImmutableBitMap>();
        for (Segment segment : segments) {
            allShows.add(segment.getAllShowIndex());
        }
        List<SegmentItem> items = new ArrayList<SegmentItem>();
        int count = 0;
        int combineNum = 0;
        List<Segment> list = new ArrayList<Segment>();
        List<ImmutableBitMap> bitMaps = new ArrayList<ImmutableBitMap>();
        for (int i = 0; i < segments.size(); i++) {
            count += allShows.get(i).getCardinality();
            combineNum += 1;
            list.add(segments.get(i));
            if (count >= capacity * 2 / 3) {
                ImmutableBitMap immutableBitMap = null;
                if (count > capacity) {
                    final MutableBitMap bitMap = BitMaps.newRoaringMutable();
                    final int[] n = new int[]{count - capacity};
                    ImmutableBitMap allShow = allShows.get(i);
                    allShow.breakableTraversal(new BreakTraversalAction() {
                        @Override
                        public boolean actionPerformed(int row) {
                            bitMap.add(row);
                            return --n[0] <= 0;
                        }
                    });
                    allShow = allShow.getAndNot(bitMap);
                    allShows.set(i, allShow);
                    i--;
                    immutableBitMap = bitMap;
                } else {
                    immutableBitMap = allShows.get(i);
                }
                bitMaps.add(immutableBitMap);
                if (combineNum != 1) {
                    items.add(new SegmentItem(new ArrayList<>(list), new ArrayList<>(bitMaps)));
                }
                count = 0;
                combineNum = 0;
                list.clear();
                bitMaps.clear();
            } else {
                bitMaps.add(allShows.get(i));
            }
        }
        if (!list.isEmpty()) {
            items.add(new SegmentItem(list, bitMaps));
        }
        return items;
    }
}
