package com.fr.swift.cloud.source.table;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.io.File;

/**
 * Created by lyon on 2019/2/28.
 */
public class TableUtils {

    /**
     * 根据版本获得对应metadata，并和db里的metadata比较
     *
     * @param versionMetadata
     * @return
     * @throws SwiftMetaDataException
     */
    public static CloudTable createIfAbsent(SwiftMetaData versionMetadata, String appId, String yearMonth) throws SwiftMetaDataException {
        SwiftMetaDataService service = SwiftContext.get().getBean(SwiftMetaDataService.class);
        String tableName = versionMetadata.getTableName();
        SwiftMetaData dbMetadata = service.getMetaDataByKey(tableName);
        CloudTable cloudTable;
        if (dbMetadata == null) {
            service.addMetaData(tableName, versionMetadata);
            dbMetadata = versionMetadata;
        }
        if (tableName.equals("execution")) {
            cloudTable = new ExecutionCSVTable(dbMetadata, appId, yearMonth);
        } else {
            cloudTable = new SwiftCSVTable(dbMetadata, appId, yearMonth);
        }
        return cloudTable;
    }

    @Deprecated
    public static CloudTable createIfAbsent(String name, String appId, String yearMonth, File... files) {
        SwiftMetaDataService service = SwiftContext.get().getBean(SwiftMetaDataService.class);
        CloudTable table = getTable(name, appId, yearMonth, files);
        SwiftMetaDataBean bean = table.createBean(SwiftDatabase.CUBE);
        String tableName = bean.getTableName();
        if (!service.containsMeta(new SourceKey(tableName))) {
            service.addMetaData(tableName, bean);
        }
        return table;
    }

    @Deprecated
    private static CloudTable getTable(String tableName, String appId, String yearMonth, File... files) {
        CloudTable table = null;
        TABLE_NAME name = TABLE_NAME.from(tableName);
        switch (name) {
            case function_usage_rate:
                table = new FunctionUsageRate(appId, yearMonth);
                break;
            case web_container:
                table = new WebContainer(appId, yearMonth);
                break;
            case template_info:
                table = new TemplateInfo(appId, yearMonth);
                break;
            case execution_sql:
                table = new ExecutionSql(appId, yearMonth);
                break;
            case package_info:
                table = new PackageInfo(appId, yearMonth);
                break;
            case real_time_usage:
                table = new RealTimeUsage(appId, yearMonth);
                break;
            case execution:
                table = new Execution(appId, yearMonth);
                break;
            case shutdown_record:
                table = new ShutdownRecord(appId, yearMonth);
                break;
            case gc_record:
                table = new GCRecord(appId, yearMonth, files);
                break;
            default:
                Crasher.crash(new RuntimeException("table name can not be empty!"));
        }
        return table;
    }

    @Deprecated
    public enum TABLE_NAME {

        empty(""),
        execution("execute"),
        execution_sql("executesql"),
        function_usage_rate("functionusagerate"),
        package_info("package"),
        real_time_usage("realtimeusage"),
        template_info("tplinfo"),
        web_container("webcontainer"),
        shutdown_record("signalRecord"),
        gc_record("fanruan.gc.log");

        private final String name;

        TABLE_NAME(String name) {
            this.name = name;
        }

        static TABLE_NAME from(String tableName) {
            if (tableName == null || tableName.isEmpty()) {
                return empty;
            } else if (tableName.equals(execution.name)) {
                return execution;
            } else if (tableName.equals(execution_sql.name)) {
                return execution_sql;
            } else if (tableName.equals(function_usage_rate.name)) {
                return function_usage_rate;
            } else if (tableName.equals(package_info.name)) {
                return package_info;
            } else if (tableName.equals(real_time_usage.name)) {
                return real_time_usage;
            } else if (tableName.equals(template_info.name)) {
                return template_info;
            } else if (tableName.equals(web_container.name)) {
                return web_container;
            } else if (tableName.equals(shutdown_record.name)) {
                return shutdown_record;
            } else if (tableName.equals(gc_record.name)) {
                return gc_record;
            }
            return empty;
        }
    }
}
