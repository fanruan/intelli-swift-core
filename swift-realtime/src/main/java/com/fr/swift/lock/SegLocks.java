package com.fr.swift.lock;

import com.fr.swift.segment.SegmentKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/11/30
 */
public interface SegLocks {

    Map<SegmentKey, SegmentKey> SEG_LOCK = new ConcurrentHashMap<>();

}
