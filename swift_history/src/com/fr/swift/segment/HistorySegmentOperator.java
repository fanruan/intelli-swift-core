package com.fr.swift.segment;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.exception.meta.SwiftMetaDataException;
//import com.fr.swift.source.MetaDataXmlManager;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.util.List;

/**
 * @author yee
 * @date 2018/1/4
 */
public class HistorySegmentOperator extends AbstractHistorySegmentOperator {

    public HistorySegmentOperator(SourceKey sourceKey, List<Segment> segments, String cubeSourceKey, SwiftResultSet swiftResultSet) throws SwiftMetaDataException {
        super(sourceKey, segments, cubeSourceKey, swiftResultSet);
        if (null != segments && !segments.isEmpty()) {
            for (int i = 0, len = segments.size(); i < len; i++) {
                this.segmentList.add(new HistorySegmentHolder(segments.get(i)));
            }
        }
    }

    @Override
    public void transport() throws Exception {
        int count = 0;
        String allotColumn = metaData.getColumnName(1);
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
            for (int i = 0, len = metaData.getColumnCount(); i < len; i++) {
                segment.putDetail(i, row.getValue(i));
            }
            segment.incrementRowCount();
        }
    }

    @Override
    public void finishTransport() {
//        MetaDataXmlManager.getManager().putMetaData(sourceKey, metaData);
        try {
            IMetaData metaData = MetaDataConvertUtil.convert2ConfigMetaData(this.metaData);
            MetaDataConfig.getInstance().addMetaData(sourceKey.getId(), metaData);
        } catch (SwiftMetaDataException e) {
            e.printStackTrace();
        }
        for (int i = 0, len = segmentList.size(); i < len; i++) {
            SegmentHolder holder = segmentList.get(i);
            holder.putRowCount();
            holder.putAllShowIndex();
            holder.putNullIndex();
            holder.release();
        }
    }

}
