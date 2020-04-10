package com.fr.swift.segment.operator.collate.segment;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyon on 2019/2/21.
 */
public class LinePartitioner implements Partitioner {

    private int capacity;

    public LinePartitioner(int capacity) {
        this.capacity = capacity;
    }

    /**
     * 尽可能少得移动数据
     * 1.按allshow升序排序
     * 2.按allshow叠加，最多不超过1000w为一个partition
     * 3.每个partition根据以下rule计算是否需要保留(即是否需要collate)
     * rules:
     * 1.块数>=5
     * 2.块数>=2 且 块数<5 且 count>=LowerLimit
     *
     * @param segmentKeys 待合并的碎片块
     * @return
     */
    @Override
    public List<SegmentPartition> partition(List<SegmentKey> segmentKeys) {
        // 先排序 从低到高
        segmentKeys.sort(Comparator.comparingInt(key -> SegmentUtils.newSegment(key).getAllShowIndex().getCardinality()));
        int count = 0;
        List<SegmentKey> list = new ArrayList<>();
        List<SegmentPartition> partitions = new ArrayList<>();
        for (SegmentKey segmentKey : segmentKeys) {
            Segment segment = SegmentUtils.newSegment(segmentKey);
            count += segment.getAllShowIndex().getCardinality();
            if (count >= capacity) {
                partitions.add(new SegmentPartition(list));
                list = new ArrayList<>();
                list.add(segmentKey);
                count = segment.getAllShowIndex().getCardinality();
            } else {
                list.add(segmentKey);
            }
        }
        if (!list.isEmpty()) {
            partitions.add(new SegmentPartition(list));
        }
        Iterator<SegmentPartition> iterator = partitions.iterator();
        while (iterator.hasNext()) {
            SegmentPartition next = iterator.next();
            //seg.size == 1且capacity>=4/5的进不来
            //2<=seg.size<=4时capacity才可能>=4/5
            if (next.getSegmentKeys().size() < 5 && next.getCardinality() < capacity * 4 / 5) {
                iterator.remove();
            }
        }
        return partitions;
    }
}
