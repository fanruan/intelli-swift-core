package com.fr.swift.segment.decrease;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.cube.io.Types;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.AbstractSegmentOperator;
import com.fr.swift.segment.HistorySegmentHolder;
import com.fr.swift.segment.RealtimeSegmentHolder;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentHolder;
import com.fr.swift.segment.column.BitmapIndexedColumn;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018-1-10 16:39:27
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 * todo 重做增量更新时候再修改此类
 */
public class DecreaseSegmentOperator extends AbstractSegmentOperator {


    private static SwiftLogger LOGGER = SwiftLoggers.getLogger(DecreaseSegmentOperator.class);

    public DecreaseSegmentOperator(SourceKey sourceKey, List<Segment> segments, String cubeSourceKey, SwiftResultSet swiftResultSet) throws SQLException {
        super(sourceKey, segments, cubeSourceKey, swiftResultSet);
        if (null != segments && !segments.isEmpty()) {
            for (int i = 0, len = segments.size(); i < len; i++) {
                if (segments.get(i).getLocation().getStoreType() == Types.StoreType.FINE_IO) {
                    this.segmentList.add(new HistorySegmentHolder(segments.get(i)));
                } else {
                    this.segmentList.add(new RealtimeSegmentHolder(segments.get(i)));
                }
            }
        }
    }


    @Override
    public void transport() throws Exception {
        long count = 0;
        String allotColumn = metaData.getColumnName(1);
        while (swiftResultSet.next()) {
            Row row = swiftResultSet.getRowData();

            for (SegmentHolder segmentHolder : segmentList) {
                Segment segment = segmentHolder.getSegment();
                DictionaryEncodedColumn dictionaryEncodedColumn = segment.getColumn(new ColumnKey(allotColumn)).getDictionaryEncodedColumn();
                BitmapIndexedColumn bitmapIndexedColumn = segment.getColumn(new ColumnKey(allotColumn)).getBitmapIndex();

                ImmutableBitMap allShowIndex = segment.getAllShowIndex();

                int decreaseIndex = dictionaryEncodedColumn.getIndex(row.getValue(0));
                ImmutableBitMap bitMap = bitmapIndexedColumn.getBitMapIndex(decreaseIndex);
                allShowIndex = allShowIndex.getAndNot(bitMap);
                segment.putAllShowIndex(allShowIndex);
            }
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
        for (int i = 0, len = segmentList.size(); i < len; i++) {
            SegmentHolder holder = segmentList.get(i);
//            holder.putRowCount();
//            holder.putAllShowIndex();
//            holder.putNullIndex();
            if (holder.getSegment().getLocation().getStoreType() == Types.StoreType.FINE_IO) {
                holder.release();
            }
        }
    }

    @Override
    public int getSegmentCount() {
        return 0;
    }
}
