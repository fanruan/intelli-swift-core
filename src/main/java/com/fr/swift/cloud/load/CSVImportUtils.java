package com.fr.swift.cloud.load;

import com.fr.swift.SwiftContext;
import com.fr.swift.cloud.source.load.SimpleSwiftResultSet;
import com.fr.swift.cloud.source.table.CSVTable;
import com.fr.swift.cloud.source.table.TableUtils;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.Table;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.insert.HistoryBlockImporter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.resultset.progress.ProgressResultSet;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyon on 2019/2/28.
 */
public class CSVImportUtils {

    public static final String treas = "treas";

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
    }
}
