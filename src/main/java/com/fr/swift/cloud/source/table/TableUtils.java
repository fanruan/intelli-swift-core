package com.fr.swift.cloud.source.table;

import com.fr.swift.SwiftContext;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.Crasher;

/**
 * Created by lyon on 2019/2/28.
 */
public class TableUtils {

    public static CSVTable createIfAbsent(String name, String appId, String yearMonth) {
        SwiftMetaDataService service = SwiftContext.get().getBean(SwiftMetaDataService.class);
        CSVTable table = getTable(name, appId, yearMonth);
        SwiftMetaDataBean bean = table.createBean(SwiftDatabase.CUBE);
        String tableName = bean.getTableName();
        if (!service.containsMeta(new SourceKey(tableName))) {
            service.addMetaData(tableName, bean);
        }
        return table;
    }

    private static CSVTable getTable(String tableName, String appId, String yearMonth) {
        CSVTable table = null;
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
            default:
                Crasher.crash(new RuntimeException("table name can not be empty!"));
        }
        return table;
    }

    public enum TABLE_NAME {

        empty(""),
        execution("execute"),
        execution_sql("executesql"),
        function_usage_rate("functionusagerate"),
        package_info("package"),
        real_time_usage("realtimeusage"),
        template_info("tplinfo"),
        web_container("webcontainer");

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
            }
            return empty;
        }
    }
}
