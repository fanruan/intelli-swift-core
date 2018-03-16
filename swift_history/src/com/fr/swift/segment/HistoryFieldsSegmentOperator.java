package com.fr.swift.segment;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.MetaDataXmlManager;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-6
 */
public class HistoryFieldsSegmentOperator extends AbstractHistorySegmentOperator {

    private List<String> fields;

    public HistoryFieldsSegmentOperator(SourceKey sourceKey, List<Segment> segments,
                                        String cubeSourceKey, SwiftResultSet swiftResultSet, List<String> fields) {
        super(sourceKey, segments, cubeSourceKey, swiftResultSet);
        this.fields = fields;
    }

    @Override
    public void transport() throws Exception {
        int count = 0;
        String allotColumn = fields.get(0);
        while (swiftResultSet.next()) {
            Row row = swiftResultSet.getRowData();
            int index = alloter.allot(count, allotColumn, row.getValue(indexOfColumn(allotColumn)));
            int size = segmentList.size();
            if (index >= size) {
                for (int i = size; i <= index; i++) {
                    segmentList.add(new HistorySegmentHolder(createSegment(i)));
                }
            } else if (index == -1) {
                index = segmentList.size() - 1;
            }
            SegmentHolder segment = segmentList.get(index);

            for (int i = 0; i < fields.size(); i++) {
                int fieldIndex = metaData.getColumnIndex(fields.get(i));
                segment.putDetail(fieldIndex, row.getValue(i));
            }
            segment.incrementRowCount();
        }
    }

    @Override
    public void finishTransport() {
//        MetaDataXmlManager.getManager().putMetaData(sourceKey, metaData);
        try {
            IMetaData metaData = convert2ConfigMetaData();
            MetaDataConfig.getInstance().addMetaData(sourceKey.getId(), metaData);
        } catch (SwiftMetaDataException e) {
            e.printStackTrace();
        }
        for (int i = 0, len = segmentList.size(); i < len; i++) {
            SegmentHolder holder = segmentList.get(i);
            holder.putNullIndex();
            holder.release();
        }
    }

    @Override
    public List<String> getIndexFields() {
        return this.fields;
    }
}
