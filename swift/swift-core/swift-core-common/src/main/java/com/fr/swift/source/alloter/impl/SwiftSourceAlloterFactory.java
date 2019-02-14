//package com.fr.swift.source.alloter.impl;
//
//import com.fr.swift.SwiftContext;
//import com.fr.swift.segment.Segment;
//import com.fr.swift.segment.SwiftSegmentManager;
//import com.fr.swift.source.SourceKey;
//import com.fr.swift.source.alloter.SwiftSourceAlloter;
//import com.fr.swift.source.alloter.impl.line.LineHistorySourceAlloter;
//import com.fr.swift.util.Util;
//
//import java.util.List;
//
///**
// * @author pony
// * @date 2017/11/22
// */
//public class SwiftSourceAlloterFactory {
//
//
//    /**
//     * @param sourceKey
//     * @param cubeSourceKey
//     * @return
//     * @description sourceKey和CubeSourcekey不相等的话，则说明是新增列，需要根据父表baseSegment来分块
//     */
//    public static SwiftSourceAlloter createLineSourceAlloter(SourceKey sourceKey, String cubeSourceKey) {
//        if (Util.equals(sourceKey.getId(), cubeSourceKey)) {
//            return new LineHistorySourceAlloter(sourceKey);
//        } else {
//            List<Segment> baseSegmentList = SwiftContext.get().getBean(SwiftSegmentManager.class).getSegment(new SourceKey(cubeSourceKey));
//            return new NewColumnSourceAlloter(baseSegmentList);
//        }
//    }
//}
