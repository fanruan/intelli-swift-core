package com.fr.swift.segment;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.cube.io.ResourceDiscoveryImpl;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.base.FakeStringDetailColumn;
import com.fr.swift.segment.column.impl.base.LongDetailColumn;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaDataImpl;
import junit.framework.TestCase;

import java.sql.Types;
import java.util.Arrays;
import java.util.List;

/**
 * @author anchore
 * @date 2017/12/25
 */
public class SegmentDemo extends TestCase {
    public void testPutSegmentMeta() {
        SwiftSegmentManager sm = new SwiftSegmentManager() {
            @Override
            public Segment getSegment(SegmentKey key) {
                return new HistorySegmentImpl(
                        new ResourceLocation("/cube/A/seg1"),
                        new SwiftMetaDataImpl("A",
                                Arrays.asList(new MetaDataColumn("long", Types.BIGINT))));
            }

            @Override
            public List<Segment> getSegment(SourceKey sourceKey) {
                return null;
            }

            @Override
            public boolean isSegmentsExist(SourceKey key) {
                return false;
            }
        };
        Segment segment = sm.getSegment(new SegmentKey("seg1"));

        segment.putRowCount(1);
        segment.putAllShowIndex(BitMaps.newAllShowBitMap(1));

        segment.release();
    }

    public void testPutLongColumn() {
        SwiftSegmentManager sm = new SwiftSegmentManager() {
            @Override
            public Segment getSegment(SegmentKey key) {
                return new HistorySegmentImpl(
                        new ResourceLocation("/cube/A/seg1"),
                        new SwiftMetaDataImpl("A",
                                Arrays.asList(new MetaDataColumn("long", Types.BIGINT))));
            }

            @Override
            public List<Segment> getSegment(SourceKey sourceKey) {
                return null;
            }

            @Override
            public boolean isSegmentsExist(SourceKey key) {
                return false;
            }
        };
        Segment segment = sm.getSegment(new SegmentKey("seg1"));
        Column<Long> longColumn = segment.getColumn(new ColumnKey("long"));
        LongDetailColumn longDetailColumn = (LongDetailColumn) longColumn.getDetailColumn();
        longDetailColumn.put(0, 1L);
        longDetailColumn.release();

        clear();

        assertEquals(longDetailColumn.getLong(0), 1);
        longDetailColumn.release();
    }

    public void testPutStringColumn() {
        SwiftSegmentManager sm = new SwiftSegmentManager() {
            @Override
            public Segment getSegment(SegmentKey key) {
                return new HistorySegmentImpl(
                        new ResourceLocation("/cube/A/seg1"),
                        new SwiftMetaDataImpl("A",
                                Arrays.asList(new MetaDataColumn("string", Types.VARCHAR))));
            }

            @Override
            public List<Segment> getSegment(SourceKey sourceKey) {
                return null;
            }

            @Override
            public boolean isSegmentsExist(SourceKey key) {
                return false;
            }
        };
        Segment segment = sm.getSegment(new SegmentKey("seg1"));
        Column<String> stringColumn = segment.getColumn(new ColumnKey("string"));
        FakeStringDetailColumn stringDetailColumn = (FakeStringDetailColumn) stringColumn.getDetailColumn();
        stringDetailColumn.put(0, "hello world");
        stringDetailColumn.release();

        clear();

        assertEquals(stringDetailColumn.get(0), "hello world");
        stringDetailColumn.release();
    }

    private void clear() {
        ResourceDiscoveryImpl.getInstance().clear();
    }
}