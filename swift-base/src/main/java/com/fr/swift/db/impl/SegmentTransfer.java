package com.fr.swift.db.impl;

import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentResultSet;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.FileUtil;
import com.fr.swift.util.function.Predicate;

import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/8/20
 */
public class SegmentTransfer {
    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    protected SegmentKey oldSegKey, newSegKey;

    private boolean index;

    public SegmentTransfer(SegmentKey oldSegKey, SegmentKey newSegKey) {
        this(oldSegKey, newSegKey, true);
    }

    public SegmentTransfer(SegmentKey oldSegKey, SegmentKey newSegKey, boolean index) {
        this.oldSegKey = oldSegKey;
        this.newSegKey = newSegKey;
        this.index = index;
    }

    public void transfer() {
        Segment oldSeg = newSegment(oldSegKey), newSeg = newSegment(newSegKey);
        Inserter inserter = new SwiftInserter(newSeg);
        try {
            SEG_SVC.addSegments(Collections.singletonList(newSegKey));

            inserter.insertData(new SegmentResultSet(oldSeg));

            indexSegmentIfNeed(newSegKey, newSeg);

            onSucceed();
        } catch (Exception e) {
            SwiftLoggers.getLogger().error("segment transfer from {} to {} failed: {}", e);
            remove(newSegKey);
        }
    }

    protected void onSucceed() {
        remove(oldSegKey);
    }

    private void indexSegmentIfNeed(SegmentKey newSegKey, Segment newSeg) throws Exception {
        if (!index || !newSeg.isHistory()) {
            return;
        }

        SwiftMetaData metadata = newSeg.getMetaData();
        // todo 暂时同步做索引
        for (int i = 0; i < metadata.getColumnCount(); i++) {
            ColumnKey columnKey = new ColumnKey(metadata.getColumnName(i + 1));
            List<Segment> segs = Collections.singletonList(newSeg);
            Table table = SwiftDatabase.getInstance().getTable(newSegKey.getTable());

            ((SwiftColumnIndexer) SwiftContext.get().getBean("columnIndexer", table, columnKey, segs)).buildIndex();

            ((SwiftColumnDictMerger) SwiftContext.get().getBean("columnDictMerger", table, columnKey, segs)).mergeDict();
        }
    }

    private void remove(final SegmentKey segKey) {
        SEG_SVC.removeSegments(Collections.singletonList(segKey));
        String absSegPath = CubeUtil.getAbsoluteSegPath(segKey);

        if (segKey.getStoreType() != StoreType.MEMORY) {
            FileUtil.delete(absSegPath);
            return;
        }

        ResourceDiscovery.getInstance().removeIf(new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.contains(CubeUtil.getSegPath(segKey));
            }
        });
        Schema swiftSchema = segKey.getSwiftSchema();
        FileUtil.delete(absSegPath.replace(swiftSchema.getDir(), swiftSchema.getBackupDir()));
    }

    private Segment newSegment(SegmentKey segKey) {
        return SegmentUtils.newSegment(segKey);
    }
}