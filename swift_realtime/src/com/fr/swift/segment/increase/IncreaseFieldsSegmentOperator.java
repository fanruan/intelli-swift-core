package com.fr.swift.segment.increase;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.RealtimeSegmentHolder;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentHolder;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;


public class IncreaseFieldsSegmentOperator extends AbstractIncreaseSegmentOperator {

    private List<String> fields;

    public IncreaseFieldsSegmentOperator(SourceKey sourceKey, SwiftMetaData metaData, List<Segment> segments,
                                         String cubeSourceKey, SwiftResultSet swiftResultSet, List<String> fields) throws SwiftMetaDataException {
        super(sourceKey, metaData, segments, cubeSourceKey, swiftResultSet);
        this.fields = fields;
    }

    @Override
    public void transport() throws Exception {
        int count = 0;
        if (!fields.isEmpty()) {
            String allotColumn = fields.get(0);
            while (swiftResultSet.next()) {
                Row row = swiftResultSet.getRowData();

                int index = alloter.allot(count, allotColumn, row.getValue(indexOfColumn(allotColumn)));
                int size = increaseSegmentList.size();
                if (index >= size) {
                    for (int i = size; i <= index; i++) {
                        increaseSegmentList.add(new RealtimeSegmentHolder(createSegment(segmentList.size() + i)));
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
            increaseSegmentList.add(new RealtimeSegmentHolder(createSegment(0)));
        }
    }

    @Override
    public void finishTransport() {
        for (int i = 0, len = increaseSegmentList.size(); i < len; i++) {
            SegmentHolder holder = increaseSegmentList.get(i);
            holder.putNullIndex();
            holder.release();
        }
    }

    @Override
    public List<String> getIndexFields() {
        return this.fields;
    }
}
