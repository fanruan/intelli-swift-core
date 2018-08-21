package com.fr.swift.segment.merge;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.Merger;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.SwiftSourceAlloterFactory;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineRowInfo;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Strings;

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
@Deprecated
public class RealtimeMerger implements Merger {
    protected static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RealtimeMerger.class);

    private SourceKey sourceKey;
    private SwiftMetaData metaData;
    private String cubeSourceKey;
    private SwiftSourceAlloter alloter;
    private List<Segment> historySegs = new ArrayList<Segment>();
    private List<Segment> realtimeSegs = new ArrayList<Segment>();
    private List<SegmentKey> configSegment;
    private int totalCount = 0;

    public RealtimeMerger(SourceKey sourceKey, SwiftMetaData metaData, String cubeSourceKey) {
        this.sourceKey = sourceKey;
        this.metaData = metaData;
        this.alloter = SwiftSourceAlloterFactory.createLineSourceAlloter(sourceKey, cubeSourceKey);
        this.cubeSourceKey = cubeSourceKey;

        List<Segment> segmentList = SwiftContext.get().getBean(SwiftSegmentManager.class).getSegment(sourceKey);
        configSegment = new ArrayList<SegmentKey>();

        for (int i = 0; i < segmentList.size(); i++) {
            Segment segment = segmentList.get(i);
            if (segment.getLocation().getStoreType() == Types.StoreType.FINE_IO) {
                historySegs.add(segment);
                createSegment(i);
            } else {
                totalCount += segment.getRowCount();
                realtimeSegs.add(segment);
            }
        }
    }

    @Override
    public List<Segment> merge() throws Exception {
        String allotColumn = metaData.getColumnName(1);
        int totalIndex = alloter.allot(new LineRowInfo(totalCount)).getOrder();
        int alloterCount = ((LineAllotRule) alloter.getAllotRule()).getStep();

        List<Segment> mergeSegments = new ArrayList<Segment>();

        SwiftResultSet resultSet = new MergerResultSet(realtimeSegs, alloterCount, metaData);

        for (int i = 1; i <= totalIndex + 1; i++) {
            Segment segment = createSegment(historySegs.size());
            mergeSegments.add(segment);

            Inserter inserter = new SwiftInserter(segment);
            inserter.insertData(resultSet);
//            resultSet.close();
        }
        release();
        return mergeSegments;
    }


    protected Segment createSegment(int order) {
        String cubePath = String.format("%s/%s/seg%d",
                metaData.getSwiftSchema().getDir(),
                cubeSourceKey, order);
        IResourceLocation location = new ResourceLocation(cubePath);
        configSegment.add(new SegmentKeyBean(sourceKey.getId(), location.getUri(), order, StoreType.FINE_IO, metaData.getSwiftSchema()));
        return SegmentUtils.newHistorySegment(location, metaData);
    }

    public void release() {
        persistMeta();
        persistSegment();
        String basePath = String.format("%s/%s",
                metaData.getSwiftSchema().getDir(), sourceKey.getId());
        ResourceDiscovery.getInstance().removeCubeResource(Strings.trimSeparator(basePath, "\\", "/"));
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
        SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class).updateSegments(sourceKey.getId(), configSegment);
    }

}
