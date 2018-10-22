package com.fr.swift.segment.container;

import com.fr.general.ComparatorUtils;
import com.fr.stable.AssistUtils;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2018/10/19
 */
public enum SegmentContainer {
    /**
     * instance
     */
    NORMAL, INDEXING;
    private final Map<SourceKey, List<SegmentPair>> tableMapSegments = new ConcurrentHashMap<SourceKey, List<SegmentPair>>();
    private final Map<String, Segment> segmentKeyMapSegment = new ConcurrentHashMap<String, Segment>();

    public boolean contains(SourceKey sourceKey) {
        return tableMapSegments.containsKey(sourceKey);
    }

    public List<Segment> getSegments(SourceKey sourceKey) {
        if (contains(sourceKey)) {
            List<SegmentPair> list = tableMapSegments.get(sourceKey);
            List<Segment> result = new ArrayList<Segment>();
            for (SegmentPair pair : list) {
                result.add(pair.getSegment());
            }
            return result;
        }
        return Collections.emptyList();
    }

    public List<Segment> getSegments(Collection<String> segmentKeys, List<String> notMatch) {
        List<Segment> list = new ArrayList<Segment>();
        for (String segmentKey : segmentKeys) {
            Segment segment = segmentKeyMapSegment.get(segmentKey);
            if (null == segment) {
                notMatch.add(segmentKey);
            } else {
                list.add(segment);
            }
        }
        return list;
    }

    public List<Segment> getSegments(List<SegmentKey> segmentKeys, List<SegmentKey> notMatch) {
        List<Segment> list = new ArrayList<Segment>();
        for (SegmentKey segmentKey : segmentKeys) {
            Segment segment = segmentKeyMapSegment.get(segmentKey.toString());
            if (null == segment) {
                notMatch.add(segmentKey);
            } else {
                list.add(segment);
            }
        }
        return list;
    }

    public synchronized void updateSegment(SegmentKey segmentKey, Segment segment) {
        segmentKeyMapSegment.put(segmentKey.toString(), segment);
        SourceKey table = segmentKey.getTable();
        if (null == tableMapSegments.get(table)) {
            tableMapSegments.put(table, new ArrayList<SegmentPair>());
        }
        List<SegmentPair> list = tableMapSegments.get(table);
        SegmentPair pair = new SegmentPair(segmentKey.toString(), segment);
        if (list.contains(pair)) {
            list.set(list.indexOf(pair), pair);
        } else {
            list.add(pair);
        }
        tableMapSegments.put(table, list);
    }

    public void clear() {
        tableMapSegments.clear();
        segmentKeyMapSegment.clear();
    }

    public List<Segment> remove(SourceKey sourceKey) {
        List<SegmentPair> list = tableMapSegments.remove(sourceKey);
        if (null == list) {
            return Collections.emptyList();
        }
        List<Segment> result = new ArrayList<Segment>();
        for (SegmentPair pair : list) {
            result.add(pair.getSegment());
            segmentKeyMapSegment.remove(pair.segmentId);
        }
        return result;
    }

    public void remove(SegmentKey segmentKey) {
        segmentKeyMapSegment.remove(segmentKey.toString());
        SourceKey table = segmentKey.getTable();
        if (tableMapSegments.containsKey(table)) {
            Iterator<SegmentPair> iterator = tableMapSegments.get(table).iterator();
            while (iterator.hasNext()) {
                if (ComparatorUtils.equals(iterator.next().getSegmentId(), segmentKey.toString())) {
                    iterator.remove();
                }
            }
        }
    }

    public static class SegmentPair {
        private String segmentId;

        private Segment segment;

        public SegmentPair(String segmentId, Segment segment) {
            this.segmentId = segmentId;
            this.segment = segment;
        }

        public String getSegmentId() {
            return segmentId;
        }

        public void setSegmentId(String segmentId) {
            this.segmentId = segmentId;
        }

        public Segment getSegment() {
            return segment;
        }

        public void setSegment(Segment segment) {
            this.segment = segment;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SegmentPair
                    && AssistUtils.equals(this.segmentId, ((SegmentPair) obj).segmentId);
        }

        @Override
        public int hashCode() {
            return AssistUtils.hashCode(this.segmentId);
        }
    }
}
