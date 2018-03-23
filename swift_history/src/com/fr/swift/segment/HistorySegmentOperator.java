package com.fr.swift.segment;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/4
 */
public class HistorySegmentOperator extends AbstractHistorySegmentOperator {
    private SwiftLogger logger = SwiftLoggers.getLogger(HistorySegmentOperator.class);
    public HistorySegmentOperator(SourceKey sourceKey, String cubeSourceKey, SwiftResultSet swiftResultSet) throws SQLException {
        super(sourceKey, cubeSourceKey, swiftResultSet);
    }

    @Override
    public void transport() throws Exception {
        long count = 0;
        String allotColumn = metaData.getColumnName(1);
        while (swiftResultSet.next()) {
            Row row = swiftResultSet.getRowData();
            int index = alloter.allot(count++, allotColumn, row.getValue(indexOfColumn(allotColumn)));
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
        try {
            IMetaData metaData = MetaDataConvertUtil.convert2ConfigMetaData(this.metaData);
            MetaDataConfig.getInstance().addMetaData(sourceKey.getId(), metaData);
        } catch (SwiftMetaDataException e) {
            logger.error("save metadata failed! ", e);
        }
        for (int i = 0, len = segmentList.size(); i < len; i++) {
            SegmentHolder holder = segmentList.get(i);
            holder.putRowCount();
            holder.putAllShowIndex();
            holder.putNullIndex();
            holder.release();
        }
        SegmentConfig.getInstance().putSegments(configSegment);
    }

}
