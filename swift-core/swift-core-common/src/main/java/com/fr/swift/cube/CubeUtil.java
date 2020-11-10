package com.fr.swift.cube;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SourceKey;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anchore
 * @date 2018/6/5
 */
public class CubeUtil {
    private static ConcurrentHashMap<String, Integer> yearMonthMap = new ConcurrentHashMap<>();

    public static boolean isReadable(Segment seg) {
        return seg.isReadable();
    }

    public static int getCurrentDir(SourceKey tableKey) {
        return 0;
    }

    public static void put(String segId, int yearMonth) {
        yearMonthMap.put(segId, yearMonth);
    }

    public static int getYearMonth(String segId) {
        return yearMonthMap.getOrDefault(segId, 0);
    }
}