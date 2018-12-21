//package com.fr.swift.source.alloter.impl;
//
//import com.fr.swift.segment.Segment;
//import com.fr.swift.source.alloter.AllotRule;
//import com.fr.swift.source.alloter.RowInfo;
//import com.fr.swift.source.alloter.SegmentInfo;
//import com.fr.swift.source.alloter.SwiftSourceAlloter;
//
//import java.util.List;
//
///**
// * This class created on 2018/4/18
// *
// * @author Lucifer
// * @description 新增列不能独立参与分块规则，只能根据父表来分块
// * @since Advanced FineBI 5.0
// */
//public class NewColumnSourceAlloter implements SwiftSourceAlloter {
//    private List<Segment> baseSegmentList;
//
//    public NewColumnSourceAlloter(List<Segment> baseSegmentList) {
//        this.baseSegmentList = baseSegmentList;
//    }
//
//    @Override
//    public SegmentInfo allot(RowInfo rowInfo) {
//        int currentSegmentsCount = baseSegmentList.get(0).getRowCount();
//        int index = 0;
//        while (rowInfo.getCursor() >= currentSegmentsCount) {
//            currentSegmentsCount += baseSegmentList.get(++index).getRowCount();
//        }
//        return new SwiftSegmentInfo(index, );
//    }
//
//    @Override
//    public AllotRule getAllotRule() {
//        return null;
//    }
//
//    @Override
//    public boolean isFull(Segment segment) {
//        return false;
//    }
//}
