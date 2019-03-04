package com.fr.swift.cloud.load;

import com.fr.swift.SwiftContext;
import com.fr.swift.cloud.source.table.TableUtils;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.Table;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.insert.HistoryBlockImporter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.resultset.importing.SwiftImportResultSet;
import com.fr.swift.source.resultset.importing.file.FileLineParser;
import com.fr.swift.source.resultset.importing.file.SingleStreamImportResultSet;
import com.fr.swift.source.resultset.importing.file.impl.CommaLineParser;
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
    private static String packageInfo = "package";

    public static void load(String path, final String appId, final String yearMonth) throws Exception {
        File[] files = new File(path).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(end);
            }
        });
        if (files == null) {
            return;
        }
        SwiftMetaDataService service = SwiftContext.get().getBean(SwiftMetaDataService.class);
        for (File file : files) {
            String name = file.getName();
            name = name.substring(0, name.indexOf(yearMonth));
            String tableName = TableUtils.createIfAbsent(name);
            final boolean isPackage = file.getName().startsWith(packageInfo);
            SwiftImportResultSet resultSet = new SingleStreamImportResultSet(
                    service.getMetaDataByKey(tableName), file.getAbsolutePath(), new CommaLineParser(true, new FileLineParser.LineParserAdaptor() {
                @Override
                public Row adapt(Row row) {
                    List list = new ArrayList();
                    for (int i = 0; i < row.getSize(); i++) {
                        list.add(row.getValue(i));
                    }
                    if (!isPackage) {
                        list.add(appId);
                    }
                    list.add(yearMonth);
                    return new ListBasedRow(list);
                }


            }) {
                @Override
                protected Row split(String line) {
                    List<String> list = new ArrayList<String>();
                    String comma = ",";
                    int start = 0;
                    while (start < line.length()) {
                        if (line.startsWith("\"", start)) {
                            int end = line.indexOf("\",");
                            if (end != -1) {
                                list.add(line.substring(start + 1, end));
                                start = end + 2;
                                continue;
                            }
                        }
                        int end = line.indexOf(comma, start);
                        list.add(line.substring(start, end));
                        start = end + 1;
                    }
                    return new ListBasedRow(list);
                }
            });
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
        }
    }
}
