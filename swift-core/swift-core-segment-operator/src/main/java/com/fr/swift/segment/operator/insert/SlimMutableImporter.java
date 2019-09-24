package com.fr.swift.segment.operator.insert;

import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.MutableResultSet;
import com.fr.swift.result.RowSwiftResultSet;
import com.fr.swift.result.SwiftMutableResultSet;
import com.fr.swift.segment.MutableCacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.split.ColumnSplitRule;
import com.fr.swift.util.IoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @Author: Bellman
 * 导入基本表，不解析 json 字段，按 id 分子表用 MutableImporter 导入
 * @Date: 2019/9/23 3:15 下午
 */
public class SlimMutableImporter<A extends SwiftSourceAlloter<?, RowInfo>> extends BaseBlockImporter<A, MutableResultSet> {

    private ColumnSplitRule[] columnSplitRules;
    private Map<SourceKey, MutableImporter> subTableImporter;
    private Map<SourceKey, SwiftMutableResultSet> subTableResultSet;
    private Map<SourceKey, SwiftMetaData> subTableMetaData;

    public SlimMutableImporter(DataSource dataSource, A alloter, ColumnSplitRule[] columnSplitRules) {
        super(dataSource, alloter);
        this.columnSplitRules = columnSplitRules;
        this.subTableImporter = new HashMap<>();
        this.subTableResultSet = new HashMap<>();
        this.subTableMetaData = new HashMap<>();
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
            while (mutableResultSet.hasNext()) {
                Row row = mutableResultSet.getNextRow();
                importRow(row);
            }
            onSucceed();
        } finally {
            // 释放每一个 resultSet 和 importer
            for (Map.Entry<SourceKey, SwiftMutableResultSet> resultSetEntry : subTableResultSet.entrySet()) {
                IoUtil.close(resultSetEntry.getValue());
            }
            for (Map.Entry<SourceKey, MutableImporter> importerEntry : subTableImporter.entrySet()) {
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

    /**
     * 把发现的子表都交给 MutableImporter 导入
     *
     * @throws Exception
     */
    protected void importRow(Row row) throws Exception {

        String id = row.getValue(0);
        String tableName = buildSubTableName(dataSource.getSourceKey().getId(), id);
        SourceKey sourceKey = new SourceKey(tableName);
        if (!SwiftDatabase.getInstance().existsTable(sourceKey)) {
            // 注意 metadata 需要改表名
            SwiftMetaData metaData = new SwiftMetaDataBean.Builder(dataSource.getMetadata()).setTableName(tableName).build();
            SwiftDatabase.getInstance().createTable(sourceKey, metaData);
            subTableMetaData.put(sourceKey, metaData);
        }
        Table table = SwiftDatabase.getInstance().getTable(sourceKey);
        if (!subTableImporter.containsKey(sourceKey)) {
            // 由于没有改 Importer 接口, 所以这里直接声明为 MutableImporter, 以便调用专有方法
            MutableImporter importer = new MutableImporter(table, alloter);
            subTableImporter.put(sourceKey, importer);
            SwiftMutableResultSet resultSet = new SwiftMutableResultSet(subTableMetaData.get(sourceKey), null, columnSplitRules);
            subTableResultSet.put(sourceKey, resultSet);
            // 初始化 importer
            importer.initImportRow(sourceKey, subTableMetaData.get(sourceKey));
        }
        List<Row> rows = new ArrayList<>();
        rows.add(row);
        RowSwiftResultSet rowResultSet = new RowSwiftResultSet(subTableMetaData.get(sourceKey), rows);
        // 最关键的一步，每次给 MutableResultSet 换一个底层 resultSet, 这样可以保留状态，避免每次 new 一个新的 Mutable
        subTableResultSet.get(sourceKey).changeResultSet(rowResultSet);
        subTableImporter.get(sourceKey).importRow(subTableResultSet.get(sourceKey));
    }

    protected String buildSubTableName(String baseTableName, String id) {
        if (id == null) {
            return baseTableName + "_" + "null";
        }
        if (id.startsWith("function")) {
            return baseTableName + "_" + "function";
        } else {
            return baseTableName + "_" + id;
        }
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

    protected class MutableInserting extends Inserting<MutableInserter> {

        public MutableInserting(MutableInserter inserter, Segment seg, int rowCount) {
            super(inserter, seg, rowCount);
        }
    }
}
