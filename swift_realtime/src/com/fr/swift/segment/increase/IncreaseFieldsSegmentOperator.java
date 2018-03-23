package com.fr.swift.segment.increase;

import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.cube.io.Types;
import com.fr.swift.segment.RealtimeSegmentHolder;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentHolder;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class IncreaseFieldsSegmentOperator extends AbstractIncreaseSegmentOperator {

    private List<String> fields;

    public IncreaseFieldsSegmentOperator(SourceKey sourceKey, List<Segment> segments,
                                         String cubeSourceKey, SwiftResultSet swiftResultSet, List<String> fields) throws SQLException {
        super(sourceKey, segments, cubeSourceKey, swiftResultSet);
        this.fields = fields;
    }

    @Override
    public void transport() throws Exception {
        long count = 0;
        if (!fields.isEmpty()) {
            String allotColumn = fields.get(0);
            while (swiftResultSet.next()) {
                Row row = swiftResultSet.getRowData();

                int index = alloter.allot(count++, allotColumn, row.getValue(indexOfColumn(allotColumn)));
                int size = increaseSegmentList.size();
                if (index >= size) {
                    for (int i = size; i <= index; i++) {
                        increaseSegmentList.add(new RealtimeSegmentHolder(createSegment(segmentList.size() + i, Types.StoreType.MEMORY)));
                    }
                } else if (index == -1) {
                    index = increaseSegmentList.size() - 1;
                }
                SegmentHolder segment = increaseSegmentList.get(index);
                for (int i = 0; i < fields.size(); i++) {
                    int fieldIndex = metaData.getColumnIndex(fields.get(i));
                    try {
                        segment.putDetail(fieldIndex, row.getValue(i));
                    } catch (Exception e) {
                        segment.putDetail(fieldIndex, null);
                    }
                }
                segment.incrementRowCount();
            }
            segmentList.addAll(increaseSegmentList);
        } else {
            if (segmentList == null || segmentList.isEmpty()) {
                increaseSegmentList.add(new RealtimeSegmentHolder(createSegment(0, Types.StoreType.MEMORY)));
            } else {
                increaseSegmentList.addAll(segmentList);
            }
        }
    }

    @Override
    public void finishTransport() {
        List<ColumnKey> columnKeys = new ArrayList<ColumnKey>();
        for (String field : fields) {
            columnKeys.add(new ColumnKey(field));
        }

        for (SegmentHolder holder : increaseSegmentList) {
            holder.putNullIndex(columnKeys);
            holder.release(columnKeys);
        }
        SegmentConfig.getInstance().putSegments(configSegment);
    }

    @Override
    public List<String> getIndexFields() {
        return this.fields;
    }
}
