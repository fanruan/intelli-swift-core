package com.fr.swift.cloud.load;

import com.fr.swift.SwiftContext;
import com.fr.swift.cloud.source.load.CSVSwiftResultSet;
import com.fr.swift.cloud.source.load.GCFileResultSet;
import com.fr.swift.cloud.source.table.CSVTable;
import com.fr.swift.cloud.source.table.GCRecord;
import com.fr.swift.cloud.source.table.TableUtils;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.insert.HistoryBlockImporter;
import com.fr.swift.service.DeleteService;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.resultset.progress.ProgressResultSet;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * Created by lyon on 2019/2/28.
 */
@Deprecated
public class CSVImportUtils {

    private static String end = ".csv";

    public static void load(String path, final String appId, final String yearMonth) throws Exception {
        SwiftLoggers.getLogger().info("base dir:\t{} ", path);
        File[] files = new File(path).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(end);
            }
        });
        if (files == null) {
            SwiftLoggers.getLogger().error("invalid dir:\t{} ", path);
            return;
        }
        final SwiftMetaDataService service = SwiftContext.get().getBean(SwiftMetaDataService.class);
        for (File file : files) {
            String name = file.getName();
            name = name.substring(0, name.indexOf(yearMonth));
            CSVTable csvTable = TableUtils.createIfAbsent(name, appId, yearMonth);
            final String tableName = csvTable.getTableName();
            deleteIfExisting(tableName, appId, yearMonth);
            SwiftLoggers.getLogger().info("start importing table:\t{} ", tableName);
            SwiftResultSet resultSet = new SimpleSwiftResultSet(file.getAbsolutePath(), csvTable.getParser(), service.getMetaDataByKey(csvTable.getTableName()));
            Table t = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey(tableName));
            HistoryLineSourceAlloter alloter = new HistoryLineSourceAlloter(new SourceKey(tableName), new LineAllotRule());
            HistoryBlockImporter importer = new HistoryBlockImporter(t, alloter);
            importer.importData(new ProgressResultSet(resultSet, tableName));
            SwiftSegmentService segmentService = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
            List<SegmentKey> segmentKeys = segmentService.getSegmentByKey(tableName);
            List<Segment> segments = new ArrayList<Segment>();
            for (SegmentKey key : segmentKeys) {
                segments.add(SegmentUtils.newSegment(key));
            }
            // 索引没有满的segment
            SegmentUtils.indexSegmentIfNeed(segments);
            SegmentUtils.releaseHisSeg(segments);
            SwiftLoggers.getLogger().info("finished importing table:\t{} ", tableName);
        }

        if (gcFiles != null && gcFiles.length > 0) {
            GCRecord gcRecord = (GCRecord) TableUtils.createIfAbsent("fanruan.gc.log", appId, yearMonth, gcFiles);
            final String tableName = gcRecord.getTableName();
            deleteIfExisting(tableName, appId, yearMonth);
            Table gcTable = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey(tableName));
            SwiftResultSet resultSet = new GCFileResultSet(gcRecord, service.getMetaDataByKey(gcRecord.getTableName()));
            HistoryLineSourceAlloter alloter = new HistoryLineSourceAlloter(new SourceKey(tableName), new LineAllotRule());
            HistoryBlockImporter importer = new HistoryBlockImporter(gcTable, alloter);
            importer.importData(new ProgressResultSet(resultSet, tableName));
            SwiftLoggers.getLogger().info("finished importing table:\t{} ", tableName);
        }
    }

    private static void deleteIfExisting(String tableName, String appId, String yearMonth) {
        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth)
                )
        );
        SwiftWhere where = new SwiftWhere(filter);
        DeleteService service = SwiftContext.get().getBean(DeleteService.class);
        try {
            service.delete(new SourceKey(tableName), where);
        } catch (Exception ignored) {
        }
    }
}
