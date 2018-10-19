package com.fr.swift.segment.container;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        List<Segment> result = new ArrayList<Segment>();
        for (SegmentPair pair : list) {
            result.add(pair.getSegment());
        }
        return result;
    }

    public void remove(SegmentKey segmentKey) {
        segmentKeyMapSegment.remove(segmentKey.toString());
        SourceKey table = segmentKey.getTable();
        if (tableMapSegments.containsKey(table)) {
            tableMapSegments.get(table).remove(segmentKey);
        }
    }

    public class SegmentPair {
        private String segmengId;

        private Segment segment;

        public SegmentPair(String segmengId, Segment segment) {
            this.segmengId = segmengId;
            this.segment = segment;
        }

        public String getSegmengId() {
            return segmengId;
        }

        public void setSegmengId(String segmengId) {
            this.segmengId = segmengId;
        }

        public Segment getSegment() {
            return segment;
        }

        public void setSegment(Segment segment) {
            this.segment = segment;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SegmentPair that = (SegmentPair) o;

            return segmengId != null ? segmengId.equals(that.segmengId) : that.segmengId == null;
        }

        @Override
        public int hashCode() {
            return segmengId != null ? segmengId.hashCode() : 0;
        }
    }
}
