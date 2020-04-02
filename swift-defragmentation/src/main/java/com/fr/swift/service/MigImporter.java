package com.fr.swift.service;

import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.CacheColumnSegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.insert.HistoryBlockImporter;
import com.fr.swift.segment.operator.insert.SwiftInserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.alloter.SegmentInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.util.IoUtil;

import java.util.Set;

/**
 * @author Heng.J
 * @date 2020/4/1
 * @description
 * @since swift 1.1
 */
public class MigImporter extends HistoryBlockImporter {

    private String yearMonth;
    private int yearMonthIndex;
    Set<String> yearMonths;

    public MigImporter(DataSource dataSource, SwiftSourceAlloter alloter, String yearMonth, Set<String> yearMonths) throws SwiftMetaDataException {
        super(dataSource, alloter);
        this.yearMonth = yearMonth;
        yearMonthIndex = dataSource.getMetadata().getColumnIndex("yearMonth");
        this.yearMonths = yearMonths;
    }

    @Override
    protected Inserting getInserting(SegmentKey segKey) {
        // FIXME 2019/3/13 anchore 有临时路径的坑 tempDir应为当前导入的路径
        ResourceLocation location = new ResourceLocation(new CubePathBuilder(segKey).setTempDir(1).build(), segKey.getStoreType());
        Segment seg = new CacheColumnSegment(location, dataSource.getMetadata());
        return new Inserting(SwiftInserter.ofOverwriteMode(seg), seg, 0);
    }


    @Override
    public void importData(SwiftResultSet swiftResultSet) throws Exception {
        try (SwiftResultSet resultSet = swiftResultSet) {
            persistMeta();

            for (int cursor = 0; resultSet.hasNext(); cursor++) {
                Row row = resultSet.getNextRow();
                if (row.getValue(yearMonthIndex - 1) == null) {
                    continue;
                }
                if (!row.getValue(yearMonthIndex - 1).equals(yearMonth)) {
                    if (yearMonths != null) {
                        yearMonths.add(row.getValue(yearMonthIndex - 1));
                    }
                    continue;
                }
                SegmentInfo segInfo = allot(cursor, row);

                if (!insertings.containsKey(segInfo)) {
                    // 可能有满了的seg
                    releaseFullIfExists();
                    indexFullIfExists();
                    SegmentKey segKey = newSegmentKey(segInfo);
                    insertings.put(segInfo, getInserting(segKey));
                    importSegKeys.add(segKey);
                }
                ((Inserting) insertings.get(segInfo)).insert(row);
            }
            IoUtil.release(this);
            processAfterSegmentDone(true);
            onSucceed();
        } catch (Throwable e) {
            SwiftLoggers.getLogger().error(e);
            IoUtil.release(this);
            processAfterSegmentDone(false);
            onFailed();
            throw e;
        }
    }
}
