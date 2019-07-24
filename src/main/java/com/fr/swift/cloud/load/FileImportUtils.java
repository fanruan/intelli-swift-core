package com.fr.swift.cloud.load;

import com.fr.swift.SwiftContext;
import com.fr.swift.cloud.source.load.CSVResultSet;
import com.fr.swift.cloud.source.load.GCFileResultSet;
import com.fr.swift.cloud.source.table.CloudTable;
import com.fr.swift.cloud.source.table.TableUtils;
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
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.resultset.progress.ProgressResultSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/5/10
 *
 * @author Lucifer
 * @description
 */
public class FileImportUtils {
    private static String csvTag = ".csv";
    private static String gcTag = "fanruan.gc.log";


    public static void load(String path, final String appId, final String yearMonth, final String version) throws Exception {
        SwiftLoggers.getLogger().info("base dir:\t{} ", path);
        Map<String, List<File>> fileImportTables = initImportTables(path, yearMonth);
        Map<String, SwiftMetaData> metaDataMap = initMetadatas(version);
        if (fileImportTables == null) {
            SwiftLoggers.getLogger().error("invalid dir:\t{} ", path);
            return;
        }

        for (Map.Entry<String, List<File>> fileImportTableEntry : fileImportTables.entrySet()) {
            String fileNameHead = fileImportTableEntry.getKey();
            SwiftMetaData versionMetadata = metaDataMap.get(fileNameHead);
            if (versionMetadata == null) {
                continue;
            }
            CloudTable cloudTable = TableUtils.createIfAbsent(versionMetadata, appId, yearMonth);
            final String tableName = cloudTable.getTableName();
            deleteIfExisting(tableName, appId, yearMonth);
            SwiftLoggers.getLogger().info("start importing table:\t{} ", tableName);
            SwiftResultSet resultSet;
            if (fileNameHead.equals(gcTag)) {
                resultSet = new GCFileResultSet(fileImportTableEntry.getValue(), cloudTable.getParser(), cloudTable.getVersionMetadata(), cloudTable.getDBMetadata());
            } else {
                resultSet = new CSVResultSet(fileImportTableEntry.getValue(), cloudTable.getParser(), cloudTable.getVersionMetadata(), cloudTable.getDBMetadata());
            }
            Table t = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey(tableName));
            HistoryLineSourceAlloter alloter = new HistoryLineSourceAlloter(new SourceKey(tableName), new LineAllotRule());
            HistoryBlockImporter importer = new HistoryBlockImporter(t, alloter);
            importer.importData(new ProgressResultSet(resultSet, tableName));
            SwiftLoggers.getLogger().info("finished importing table:\t{} ", tableName);
        }
    }

    /**
     * @param path
     * @param yearMonth
     * @return key:filenameHeader  value:file list
     * @throws Exception
     */
    public static Map<String, List<File>> initImportTables(String path, String yearMonth) throws Exception {
        File file = new File(path);
        File[] importFiles = file.listFiles();
        Map<String, List<File>> importTables = new HashMap<String, List<File>>();
        for (File importFile : importFiles) {
            String fileName;
            if (importFile.getName().endsWith(csvTag)) {
                fileName = importFile.getName().split(yearMonth)[0];
            } else if (importFile.getName().startsWith(gcTag)) {
                fileName = gcTag;
            } else {
                continue;
            }
            if (!importTables.containsKey(fileName)) {
                importTables.put(fileName, new ArrayList<File>());
            }
            importTables.get(fileName).add(importFile);
        }
        return importTables;
    }

    public static Map<String, SwiftMetaData> initMetadatas(String version) throws Exception {
        return CloudVersionProperty.getProperty().getMetadataMapByVersion(version);
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
