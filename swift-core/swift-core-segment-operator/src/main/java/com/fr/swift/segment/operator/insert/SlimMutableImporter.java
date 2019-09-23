package com.fr.swift.segment.operator.insert;

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
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.MutableCacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.segment.event.SyncSegmentLocationEvent;
import com.fr.swift.segment.operator.Importer;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.resultset.progress.MutableProgressResultSet;
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
    private Map<String, List<Row>> subTables;

    public SlimMutableImporter(DataSource dataSource, A alloter, ColumnSplitRule[] columnSplitRules) {
        super(dataSource, alloter);
        this.columnSplitRules = columnSplitRules;
        this.subTables = new HashMap<>();
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
                detectSubTable(row);
            }
            importSubTableData();
            onSucceed();
        } finally {
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
     * 把 id 提取出来构建新的表名，并把当前行存起来
     *
     * @param row
     */
    private void detectSubTable(Row row) {
        String id = row.getValue(0);
        String tableName = buildSubTableName(dataSource.getSourceKey().getId(), id);
        if (subTables.containsKey(tableName)) {
            subTables.get(tableName).add(row);
        } else {
            List<Row> rows = new ArrayList<>();
            rows.add(row);
            subTables.put(tableName, rows);
        }
    }

    /**
     * 把发现的子表都交给 MutableImporter 导入
     *
     * @throws Exception
     */
    protected void importSubTableData() throws Exception {
        for (Map.Entry<String, List<Row>> entry : subTables.entrySet()) {
            if (!SwiftDatabase.getInstance().existsTable(new SourceKey(entry.getKey()))) {
                SwiftDatabase.getInstance().createTable(new SourceKey(entry.getKey()), dataSource.getMetadata());
            }
            Table table = SwiftDatabase.getInstance().getTable(new SourceKey(entry.getKey()));
            Importer importer = new MutableImporter(table, alloter);
            SwiftMutableResultSet resultSet = new SwiftMutableResultSet(table.getMetadata(), new RowSwiftResultSet(table.getMetadata(), entry.getValue()), columnSplitRules);
            SwiftResultSet finalResultSet = new MutableProgressResultSet(resultSet, entry.getKey());
            importer.importData(finalResultSet);
        }
    }

    protected String buildSubTableName(String baseTableName, String id) {
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
