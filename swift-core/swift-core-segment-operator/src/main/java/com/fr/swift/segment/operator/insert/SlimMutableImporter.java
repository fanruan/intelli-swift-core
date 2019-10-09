package com.fr.swift.segment.operator.insert;

import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.MutableResultSet;
import com.fr.swift.segment.MutableCacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.BaseSourceAlloter;
import com.fr.swift.util.IoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @Author: Bellman
 * 只负责统筹row和MutableImporter，其他都交给MutableResultSet和MutableImporter
 * @Date: 2019/9/23 3:15 下午
 */
public class SlimMutableImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, MutableResultSet> {

    private Map<String, MutableImporter> subTableImporter;

    public SlimMutableImporter(DataSource dataSource, A alloter) {
        super(dataSource, alloter);
        this.subTableImporter = new HashMap<>();
    }

    /**
     * 不需要存父表
     *
     * @param mutableResultSet
     * @throws Exception
     */
    @Override
    public void importData(MutableResultSet mutableResultSet) throws Exception {
        try {
            persistMeta();
            while (mutableResultSet.hasNext()) {
                Row row = mutableResultSet.getNextRow();
                String subTableName = mutableResultSet.getCurrentTableName();
                if (!subTableImporter.containsKey(subTableName)) {
                    Table table;
                    if (!SwiftDatabase.getInstance().existsTable(new SourceKey(subTableName))) {
                        table = SwiftDatabase.getInstance().createTable(new SourceKey(subTableName), mutableResultSet.getMetaData());
                    } else {
                        table = SwiftDatabase.getInstance().getTable(new SourceKey(subTableName));
                    }
                    BaseSourceAlloter subAlloter = (BaseSourceAlloter) ((BaseSourceAlloter) alloter).clone();
                    subAlloter.setSourceKey(new SourceKey(subTableName));
                    MutableImporter mutableImporter = new MutableImporter(table, subAlloter);
                    mutableImporter.persistMeta();
                    subTableImporter.put(subTableName, mutableImporter);
                }
                MutableImporter mutableImporter = subTableImporter.get(subTableName);
                mutableImporter.importRow(mutableResultSet, row);
            }
            onSucceed();
        } catch (Throwable e) {
            SwiftLoggers.getLogger().error(e);
            onFailed();
            throw e;
        } finally {
            for (Map.Entry<String, MutableImporter> importerEntry : subTableImporter.entrySet()) {
                importerEntry.getValue().finishImportRow();
            }
            IoUtil.close(mutableResultSet);
            IoUtil.release(this);
        }
    }

    @Override
    protected MutableInserting getInserting(SegmentKey segKey) {
        // FIXME 2019/3/13 anchore 有临时路径的坑 tempDir应为当前导入的路径
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(CubeUtil.getCurrentDir(segKey.getTable())).build(), segKey.getStoreType());
        Segment seg = new MutableCacheColumnSegment(location, dataSource.getMetadata());
        return new MutableInserting(MutableInserter.ofOverwriteMode(seg), seg, 0);
    }

    @Override
    protected void handleFullSegment(SegmentInfo segInfo) {
        try {
            SegmentKey segKey = newSegmentKey(segInfo);
            SwiftEventDispatcher.syncFire(SegmentEvent.UPLOAD_HISTORY, segKey);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    protected void onSucceed() {
        segLocationSvc.saveOrUpdateLocal(new HashSet<>(importSegKeys));
        SwiftEventDispatcher.fire(SyncSegmentLocationEvent.PUSH_SEG, importSegKeys);
    }

    @Override
    protected void onFailed() {
        // do nothing
    }

    @Override
    protected void indexIfNeed(SegmentInfo segInfo) {
        try {
            // FIXME 2019/3/13 anchore 有临时路径的坑 tempDir应为当前导入的路径
            // todo 考虑异步，提升性能
            SegmentUtils.indexSegmentIfNeed(Collections.singletonList(insertings.get(segInfo).getSegment()));
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public List<SegmentKey> getImportSegments() {
        List<SegmentKey> segmentKeys = new ArrayList<>();
        for (Map.Entry<String, MutableImporter> entry : subTableImporter.entrySet()) {
            segmentKeys.addAll(entry.getValue().getImportSegments());
        }
        return segmentKeys;
    }

    protected class MutableInserting extends Inserting<MutableInserter> {
        public MutableInserting(MutableInserter inserter, Segment seg, int rowCount) {
            super(inserter, seg, rowCount);
        }
    }
}
