package com.fr.swift.segment.operator.record;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Recorder;
import com.fr.swift.segment.operator.utils.InserterUtils;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class created on 2018/5/23
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RealtimeRecorder implements Recorder {

    private SourceKey sourceKey;
    private SwiftMetaData swiftMetaData;
    private List<String> fields;
    protected String cubeSourceKey;

    private Map<Integer, Segment> segmentMap;

    protected Map<String, MutableBitMap> nullMap;
    private int rowCount = 0;
    private List<Column> columnList;
    private List<ColumnTypeConstants.ClassType> classTypeList;

    private Segment currentSegment = null;

    public RealtimeRecorder(SourceKey sourceKey, SwiftMetaData swiftMetaData, List<String> fields, String cubeSourceKey) {
        this.sourceKey = sourceKey;
        this.swiftMetaData = swiftMetaData;
        this.fields = fields;
        this.cubeSourceKey = cubeSourceKey;
        this.segmentMap = new HashMap<Integer, Segment>();
    }

    @Override
    public void recordData(Row row, int segIndex) {
        if (segmentMap.containsKey(segIndex)) {
            currentSegment = segmentMap.get(segIndex);
        } else {
            currentSegment = createSegment(segIndex);
            segmentMap.put(segIndex, currentSegment);
            resetElement(currentSegment);
        }
        putRow(row);
        rowCount++;
        currentSegment.putRowCount(rowCount);
    }

    @Override
    public void end() {
        for (Segment segment : segmentMap.values()) {
            for (String field : fields) {
                segment.getColumn(new ColumnKey(field)).getBitmapIndex().release();
                segment.getColumn(new ColumnKey(field)).getDetailColumn().release();
            }
            segment.release();
        }
    }

    private void resetElement(Segment segment) {
        try {
            nullMap = new ConcurrentHashMap<String, MutableBitMap>();
            rowCount = 0;
            columnList = new ArrayList<Column>();
            classTypeList = new ArrayList<ColumnTypeConstants.ClassType>();

            for (String field : fields) {
                SwiftMetaDataColumn metaDataColumn = swiftMetaData.getColumn(field);
                ColumnTypeConstants.ClassType clazz = ColumnTypeUtils.getClassType(metaDataColumn);
                ColumnKey columnKey = new ColumnKey(metaDataColumn.getName());
                Column column = segment.getColumn(columnKey);
                columnList.add(column);
                classTypeList.add(clazz);
                nullMap.put(metaDataColumn.getName(), BitMaps.newRoaringMutable());
            }
        } catch (Exception e) {
        }
    }

    private void putRow(Row rowData) {
        for (int i = 0; i < fields.size(); i++) {
            if (InserterUtils.isBusinessNullValue(rowData.getValue(i))) {
                columnList.get(i).getDetailColumn().put(rowCount, InserterUtils.getNullValue(classTypeList.get(i)));
                InserterUtils.setNullIndex(fields.get(i), rowCount, nullMap);
                currentSegment.getColumn(new ColumnKey(fields.get(i))).getBitmapIndex().putNullIndex(nullMap.get(fields.get(i)));
            } else {
                columnList.get(i).getDetailColumn().put(rowCount, rowData.getValue(i));
            }
        }
    }

    private Segment createSegment(int index) {
        String cubePath = ResourceDiscovery.getInstance().getCubePath() + "/" + cubeSourceKey + "/seg" + index;
        IResourceLocation location = new ResourceLocation(cubePath, Types.StoreType.FINE_IO);
        return new HistorySegmentImpl(location, swiftMetaData);
    }
}
