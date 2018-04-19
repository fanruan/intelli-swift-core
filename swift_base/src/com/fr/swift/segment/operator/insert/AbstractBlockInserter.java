package com.fr.swift.segment.operator.insert;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.ISegmentKey;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.unique.SegmentKeyUnique;
import com.fr.swift.config.unique.SegmentUnique;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentIndexCache;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.utils.InserterUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceAlloter;
import com.fr.swift.source.SwiftSourceAlloterFactory;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/3/27
 *
 * @author Lucifer
 * @description 指定数据，分块逻辑在内部计算
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractBlockInserter implements Inserter {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstractBlockInserter.class);

    protected SourceKey sourceKey;
    protected String cubeSourceKey;
    protected SwiftMetaData swiftMetaData;
    protected List<String> fields;
    protected List<Segment> segments;
    protected IConfigSegment configSegment;
    protected SwiftSourceAlloter alloter;
    protected SegmentIndexCache segmentIndexCache;
    private int startSegIndex;

    public AbstractBlockInserter(SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData) {
        this(sourceKey, cubeSourceKey, swiftMetaData, swiftMetaData.getFieldNames());
    }

    public AbstractBlockInserter(SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData, List<String> fields) {
        this(new ArrayList<Segment>(), sourceKey, cubeSourceKey, swiftMetaData, fields);
    }

    public AbstractBlockInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData) {
        this(segments, sourceKey, cubeSourceKey, swiftMetaData, swiftMetaData.getFieldNames());
    }

    public AbstractBlockInserter(List<Segment> segments, SourceKey sourceKey, String cubeSourceKey, SwiftMetaData swiftMetaData, List<String> fields) {
        this.sourceKey = sourceKey;
        this.cubeSourceKey = cubeSourceKey;
        this.swiftMetaData = swiftMetaData;
        this.fields = fields;
        this.alloter = SwiftSourceAlloterFactory.createLineSourceAlloter(sourceKey, cubeSourceKey);
        this.segments = new ArrayList<Segment>();
        this.configSegment = new SegmentUnique();
        this.configSegment.setSourceKey(sourceKey.getId());
        this.segments = segments;
        this.segmentIndexCache = new SegmentIndexCache();
        this.startSegIndex = segments.size();
        for (int i = 0; i < segments.size(); i++) {
            if (segments.get(i).isHistory()) {
                createSegment(i, Types.StoreType.FINE_IO);
            } else {
                createSegment(i, Types.StoreType.MEMORY);
            }
        }
    }

    @Override
    public boolean insertData(List<Row> rowList) {
        return false;
    }

    @Override
    public boolean insertData(SwiftResultSet swiftResultSet) throws Exception {
        if (!fields.isEmpty()) {
            try {
                long count = 0;
                String allotColumn = fields.get(0);
                while (swiftResultSet.next()) {
                    Row rowData = swiftResultSet.getRowData();
                    // fixme 为啥这里要传后两个参数？
                    // 为了以后特殊的分块逻辑
                    int size = segments.size();
                    int index = alloter.allot(count, allotColumn, rowData.getValue(0)) + startSegIndex;
                    if (index >= size) {
                        for (int i = size; i <= index; i++) {
                            segmentIndexCache.putSegRow(i, 0);
                            segments.add(createSegment(i));
                        }
                    } else if (index == -1) {
                        index = segments.size() - 1;
                    }
                    int segmentRow = segmentIndexCache.getSegRowByIndex(index);

                    Segment segment = segments.get(index);
                    segmentIndexCache.putSegment(index, segment);
                    for (int i = 0; i < fields.size(); i++) {
                        if (InserterUtils.isBusinessNullValue(rowData.getValue(i))) {
                            SwiftMetaDataColumn metaDataColumn = swiftMetaData.getColumn(fields.get(i));
                            ColumnTypeConstants.ClassType clazz = ColumnTypeUtils.getClassType(metaDataColumn);
                            segment.getColumn(new ColumnKey(fields.get(i))).getDetailColumn().put(segmentRow, InserterUtils.getNullValue(clazz));
                            segmentIndexCache.putSegFieldNull(index, fields.get(i), segmentRow);
                        } else {
                            segment.getColumn(new ColumnKey(fields.get(i))).getDetailColumn().put(segmentRow, rowData.getValue(i));
                        }
                    }
                    segmentIndexCache.putSegRow(index, ++segmentRow);
                    count++;
                }
            } finally {
                swiftResultSet.close();
            }
        } else {
            List<Segment> cubeSourceSegments = SwiftContext.getInstance().getSegmentProvider().getSegment(new SourceKey(cubeSourceKey));
            for (int i = 0; i < cubeSourceSegments.size(); i++) {
                Segment segment = cubeSourceSegments.get(i);
                createSegment(i, segment.isHistory() ? Types.StoreType.FINE_IO : Types.StoreType.MEMORY);
            }
        }
        release();
        return true;
    }

    protected abstract Segment createSegment(int order);

    /**
     * 创建Segment
     * TODO 分块存放目录
     *
     * @param order 块号
     * @return
     * @throws Exception
     */
    protected Segment createSegment(int order, Types.StoreType storeType) {
        String cubePath = System.getProperty("user.dir") + "/cubes/" + cubeSourceKey + "/seg" + order;
        IResourceLocation location = new ResourceLocation(cubePath, storeType);
        ISegmentKey segmentKey = new SegmentKeyUnique();
        segmentKey.setSegmentOrder(order);
        segmentKey.setUri(location.getUri().getPath());
        segmentKey.setSourceId(sourceKey.getId());
        segmentKey.setStoreType(storeType.name());
        configSegment.addSegment(segmentKey);
        return createNewSegment(location, swiftMetaData);
    }

    protected abstract Segment createNewSegment(IResourceLocation location, SwiftMetaData swiftMetaData);

    public void release() {
        persistMeta();
        persistSegment();

        for (Map.Entry<Integer, Segment> entry : segmentIndexCache.getNewSegMap().entrySet()) {
            Segment segment = entry.getValue();
            segment.putAllShowIndex(BitMaps.newAllShowBitMap(segmentIndexCache.getSegRowByIndex(entry.getKey())));
            segment.putRowCount(segmentIndexCache.getSegRowByIndex(entry.getKey()));
            if (segment.isHistory()) {
                for (String field : fields) {
                    segment.getColumn(new ColumnKey(field)).getBitmapIndex().putNullIndex(segmentIndexCache.getNullBySegAndField(entry.getKey(), field));
                    segment.getColumn(new ColumnKey(field)).getBitmapIndex().release();
                    segment.getColumn(new ColumnKey(field)).getDetailColumn().release();
                }
                segment.release();
            } else {
                for (String field : fields) {
                    segment.getColumn(new ColumnKey(field)).getBitmapIndex().putNullIndex(segmentIndexCache.getNullBySegAndField(entry.getKey(), field));
                }
            }
        }
    }

    protected void persistMeta() {
        try {
            if (!SwiftDatabase.getInstance().existsTable(sourceKey)) {
                SwiftDatabase.getInstance().createTable(sourceKey, swiftMetaData);
            }
        } catch (SQLException e) {
            LOGGER.error("save metadata failed! ", e);
            Crasher.crash(e);
        }
    }

    protected void persistSegment() {
        SegmentConfig.getInstance().putSegment(configSegment);
    }

    @Override
    public List<String> getFields() {
        return fields;
    }
}
