package com.fr.swift.segment.increase;

import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.RealtimeSegmentHolder;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentHolder;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018-1-10 10:54:55
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class IncreaseSegmentOperator extends AbstractIncreaseSegmentOperator {

    private static SwiftLogger LOGGER = SwiftLoggers.getLogger(IncreaseSegmentOperator.class);

    public IncreaseSegmentOperator(SourceKey sourceKey, List<Segment> segments,
                                   String cubeSourceKey, SwiftResultSet swiftResultSet) throws SQLException {
        super(sourceKey, segments, cubeSourceKey, swiftResultSet);
    }

    @Override
    public void transport() throws Exception {
        long count = 0;
        if (metaData.getColumnCount() != 0) {
            String allotColumn = metaData.getColumnName(1);
            while (swiftResultSet.next()) {
                Row row = swiftResultSet.getRowData();

                int index = alloter.allot(count++, allotColumn, row.getValue(indexOfColumn(allotColumn)));
                int size = increaseSegmentList.size();
                if (index >= size) {
                    for (int i = size; i <= index; i++) {
                        increaseSegmentList.add(new RealtimeSegmentHolder(createSegment(segmentList.size() + i)));
                    }
                } else if (index == -1) {
                    index = increaseSegmentList.size() - 1;
                }
                SegmentHolder segment = increaseSegmentList.get(index);
                for (int i = 0, len = metaData.getColumnCount(); i < len; i++) {
                    try {
                        segment.putDetail(i, row.getValue(i));
                    } catch (Exception e) {
                        segment.putDetail(i, null);
                    }
                }
                segment.incrementRowCount();
            }
            segmentList.addAll(increaseSegmentList);
        }
    }


    @Override
    public void finishTransport() {

        try {
            IMetaData metaData = MetaDataConvertUtil.convert2ConfigMetaData(this.metaData);
            MetaDataConfig.getInstance().addMetaData(sourceKey.getId(), metaData);
        } catch (SwiftMetaDataException e) {
            LOGGER.error("save metadata failed! ", e);
        }

        for (int i = 0, len = increaseSegmentList.size(); i < len; i++) {
            SegmentHolder holder = increaseSegmentList.get(i);
            holder.putRowCount();
            holder.putAllShowIndex();
            holder.putNullIndex();
            holder.release();
        }
        SegmentConfig.getInstance().putSegments(configSegment);
    }

    @Override
    public int getSegmentCount() {
        return 0;
    }
}
