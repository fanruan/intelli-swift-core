package com.fr.swift.generate.segment.operator.merger;

import com.fr.general.ComparatorUtils;
import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftConfigServiceProvider;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.Merger;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
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
 * @description 增量更新后realtime segment合并。
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeMerger implements Merger {

    protected SwiftLogger LOGGER = SwiftLoggers.getLogger(RealtimeMerger.class);

    protected SourceKey sourceKey;
    protected SwiftMetaData metaData;
    protected String cubeSourceKey;
    protected SwiftSourceAlloter alloter;
    protected List<Segment> historySegmentList = new ArrayList<Segment>();
    protected List<Segment> realtimeSegmentList = new ArrayList<Segment>();
    protected List<SegmentKey> configSegment;
    protected int totalCount = 0;

    public RealtimeMerger(SourceKey sourceKey, SwiftMetaData metaData, String cubeSourceKey) {
        this.sourceKey = sourceKey;
        this.metaData = metaData;
        this.alloter = SwiftSourceAlloterFactory.createLineSourceAlloter(sourceKey, cubeSourceKey);
        this.cubeSourceKey = cubeSourceKey;

        List<Segment> segmentList = SwiftContext.getInstance().getBean(SwiftSegmentManager.class).getSegment(sourceKey);
        configSegment = new ArrayList<SegmentKey>();

        for (int i = 0; i < segmentList.size(); i++) {
            Segment segment = segmentList.get(i);
            if (ComparatorUtils.equals(segment.getLocation().getStoreType(), Types.StoreType.FINE_IO)) {
                historySegmentList.add(segment);
                createSegment(i);
            } else {
                totalCount += segment.getRowCount();
                realtimeSegmentList.add(segment);
            }
        }
    }

    @Override
    public List<Segment> merge() throws Exception {
        String allotColumn = metaData.getColumnName(1);
        int totalIndex = alloter.allot(totalCount, allotColumn, null);
        int alloterCount = alloter.getAllotStep();

        List<Segment> mergeSegments = new ArrayList<Segment>();

        SwiftResultSet resultSet = new MergerResultSet(realtimeSegmentList, alloterCount, metaData);

        for (int i = 1; i <= totalIndex + 1; i++) {
            Segment segment = createSegment(historySegmentList.size());
            mergeSegments.add(segment);

            Inserter inserter = new MergerInserter(segment);
            inserter.insertData(resultSet);
//            resultSet.close();
        }
        release();
        return mergeSegments;
    }


    protected Segment createSegment(int order) {
        String cubePath = String.format("%s/%s/seg%d",
                SwiftCubePathConfig.getInstance().getPath(),
                cubeSourceKey, order);
        IResourceLocation location = new ResourceLocation(cubePath);
        configSegment.add(new SegmentKeyBean(sourceKey.getId(), location.getUri(), order, StoreType.FINE_IO));
        return new HistorySegmentImpl(location, metaData);
    }

    public void release() {
        persistMeta();
        persistSegment();
        ResourceDiscovery.getInstance().removeCubeResource(String.format("%s/%s",
                SwiftCubePathConfig.getInstance().getPath(), sourceKey.getId()));
    }

    protected void persistMeta() {
        try {
            if (!SwiftDatabase.getInstance().existsTable(sourceKey)) {
                SwiftDatabase.getInstance().createTable(sourceKey, metaData);
            }
        } catch (SQLException e) {
            LOGGER.error("save metadata failed! ", e);
            Crasher.crash(e);
        }
    }

    protected void persistSegment() {
        SwiftConfigServiceProvider.getInstance().addSegments(configSegment);
    }

}
