package com.fr.swift.segment.operator.merge;

import com.fr.general.ComparatorUtils;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.IMetaData;
import com.fr.swift.config.conf.MetaDataConfig;
import com.fr.swift.config.conf.MetaDataConvertUtil;
import com.fr.swift.config.conf.SegmentConfig;
import com.fr.swift.config.unique.SegmentUnique;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentIndexCache;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.segment.operator.Merger;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftSourceAlloter;
import com.fr.swift.source.SwiftSourceAlloterFactory;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/3/28
 *
 * @author Lucifer
 * @description todo
 * @since Advanced FineBI Analysis 1.0
 */
public abstract class AbstractMerger implements Merger {

    protected SwiftLogger LOGGER = SwiftLoggers.getLogger(AbstractMerger.class);

    protected SourceKey sourceKey;
    protected SwiftMetaData metaData;
    protected String cubeSourceKey;
    protected SwiftSourceAlloter alloter;
    protected List<Segment> historySegmentList = new ArrayList<Segment>();
    protected List<Segment> realtimeSegmentList = new ArrayList<Segment>();
    protected List<Segment> newHisSegmentList = new ArrayList<Segment>();
    protected SegmentIndexCache segmentIndexCache;
    protected IConfigSegment configSegment;

    public AbstractMerger(SourceKey sourceKey, SwiftMetaData metaData, String cubeSourceKey) throws SQLException {
        this.sourceKey = sourceKey;
        this.metaData = metaData;
        this.alloter = SwiftSourceAlloterFactory.createSourceAlloter(sourceKey);
        this.cubeSourceKey = cubeSourceKey;

        List<Segment> segmentList = SwiftContext.getInstance().getSegmentProvider().getSegment(sourceKey);
        configSegment = new SegmentUnique();
        this.configSegment.setSourceKey(sourceKey.getId());

        for (int i = 0; i < segmentList.size(); i++) {
            Segment segment = segmentList.get(i);
            if (ComparatorUtils.equals(segment.getLocation().getStoreType(), Types.StoreType.FINE_IO)) {
                historySegmentList.add(segment);
                createSegment(i);
            } else {
                realtimeSegmentList.add(segment);
            }
        }
        this.segmentIndexCache = new SegmentIndexCache();
    }

    @Override
    public boolean merge() throws Exception {
        int count = 0;
        String allotColumn = metaData.getColumnName(1);

        for (Segment segment : realtimeSegmentList) {
            int currentCount = 0;
            int segCount = segment.getRowCount();
            int columnCount = metaData.getColumnCount();

            List<DetailColumn> columnList = new ArrayList<DetailColumn>();
            for (int i = 0; i < columnCount; i++) {
                columnList.add(segment.getColumn(new ColumnKey(metaData.getColumnName(i + 1))).getDetailColumn());
            }

            while (currentCount < segCount) {
                int index = alloter.allot(count++, allotColumn, null);
                if (index >= newHisSegmentList.size()) {
                    for (int i = newHisSegmentList.size(); i <= index; i++) {
                        newHisSegmentList.add(createSegment(i + historySegmentList.size()));
                    }
                } else if (index == -1) {
                    index = newHisSegmentList.size() - 1;
                }
                Segment newSegment = newHisSegmentList.get(index);
                segmentIndexCache.putSegment(index, segment);
//                for (int i = 0; i < columnCount; i++) {
//                    segmentHolder.putDetail(i, columnList.get(i).get(currentCount));
//                }
//                segmentIndexCache.putSegRow(index, ++segmentRow);
//                currentCount++;
            }
        }
        historySegmentList.addAll(newHisSegmentList);
        return true;
    }


    public void release() {
        persistMeta();
        persistSegment();
    }

    protected void persistMeta() {
        try {
            IMetaData metaData = MetaDataConvertUtil.convert2ConfigMetaData(this.metaData);
            MetaDataConfig.getInstance().addMetaData(sourceKey.getId(), metaData);
        } catch (SwiftMetaDataException e) {
            LOGGER.error("save metadata failed! ", e);
            Crasher.crash(e);
        }
    }

    protected void persistSegment() {
        SegmentConfig.getInstance().putSegment(configSegment);
    }

    protected abstract Segment createSegment(int order);
}
