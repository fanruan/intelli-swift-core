package com.fr.swift.source.alloter;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftSourceAlloter;

import java.util.List;

/**
 * This class created on 2018/4/18
 *
 * @author Lucifer
 * @description 新增列不能独立参与分块规则，只能根据父表来分块
 * @since Advanced FineBI 5.0
 */
public class NewColumnSourceAlloter implements SwiftSourceAlloter {

    private List<Segment> baseSegmentList;

    public NewColumnSourceAlloter(List<Segment> baseSegmentList) {
        this.baseSegmentList = baseSegmentList;
    }

    @Override
    public int allot(long row, String keyColumn, Object data) {
        int currentSegmentsCount = baseSegmentList.get(0).getRowCount();
        int index = 0;
        while (row >= currentSegmentsCount) {
            currentSegmentsCount += baseSegmentList.get(++index).getRowCount();
        }
        return index;
    }

    @Override
    public int getAllotStep() {
        return 0;
    }
}
