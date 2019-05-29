package com.fr.swift.cloud.yaml;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.cloud.load.CloudVersionProperty;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import org.ho.yaml.Yaml;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/5/13
 *
 * @author Lucifer
 * @description 生成v1.0 metadata的yaml文件
 */
public class CloudMetadataCreatorV1_0 {

    public static void main(String[] args) throws Exception {
        Map<String, SwiftMetaData> map = new LinkedHashMap<>();
        map.put(Execution.fileName, new Execution().getMetadata());
        map.put(ExecutionSql.fileName, new ExecutionSql().getMetadata());
        map.put(FunctionUsageRate.fileName, new FunctionUsageRate().getMetadata());
        map.put(PackageInfo.fileName, new PackageInfo().getMetadata());
        map.put(RealTimeUsage.fileName, new RealTimeUsage().getMetadata());
        map.put(TemplateInfo.fileName, new TemplateInfo().getMetadata());
        map.put(WebContainer.fileName, new WebContainer().getMetadata());
        Yaml.dump(map, new File("cloud.v1.0.yaml"));
        Map<String, SwiftMetaData> metaDataMap = Yaml.loadType(new File("cloud.v1.0.yaml"), HashMap.class);
        Map<String, SwiftMetaData> metaDataMap1 = CloudVersionProperty.getProperty().getMetadataMapByVersion("1.0");
    }

    private static class Execution {
        public static final String tableName = "execution";
        public static final String fileName = "execute";

        public SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
        public SwiftMetaDataColumn tName = new MetaDataColumnBean("tName", Types.VARCHAR);
        public SwiftMetaDataColumn displayName = new MetaDataColumnBean("displayName", Types.VARCHAR);
        public SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        public SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
        public SwiftMetaDataColumn type = new MetaDataColumnBean("type", Types.BIGINT);
        public SwiftMetaDataColumn consume = new MetaDataColumnBean("consume", Types.BIGINT);
        public SwiftMetaDataColumn sqlTime = new MetaDataColumnBean("sqlTime", Types.BIGINT);
        public SwiftMetaDataColumn reportId = new MetaDataColumnBean("reportId", Types.VARCHAR);
        public SwiftMetaDataColumn coreConsume = new MetaDataColumnBean("coreConsume", Types.BIGINT);
        public SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        public SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(id, tName, displayName, time, memory, type, consume, sqlTime, reportId, coreConsume, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    private static class ExecutionSql {
        public static final String tableName = "execution_sql";
        public static final String fileName = "executesql";

        public SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        public SwiftMetaDataColumn executionId = new MetaDataColumnBean("executionId", Types.VARCHAR);
        public SwiftMetaDataColumn dsName = new MetaDataColumnBean("dsName", Types.VARCHAR);
        public SwiftMetaDataColumn sqlTime = new MetaDataColumnBean("sqlTime", Types.BIGINT);
        public SwiftMetaDataColumn rows = new MetaDataColumnBean("rows", Types.BIGINT);
        public SwiftMetaDataColumn columns = new MetaDataColumnBean("columns", Types.BIGINT);
        public SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        public SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, executionId, dsName, sqlTime, rows, columns, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    public static class FunctionUsageRate {

        public static final String tableName = "function_usage_rate";
        public static final String fileName = "functionusagerate";

        private SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(id, time, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    public static class PackageInfo {

        public static final String tableName = "package_info";
        public static final String fileName = "package";

        private SwiftMetaDataColumn appName = new MetaDataColumnBean("appName", Types.VARCHAR);
        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(appName, appId, time, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    public static class RealTimeUsage {

        public static final String tableName = "real_time_usage";
        public static final String fileName = "realtimeusage";

        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn cpu = new MetaDataColumnBean("cpu", Types.DOUBLE);
        private SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, cpu, memory, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    public static class TemplateInfo {

        public static final String tableName = "template_info";
        public static final String fileName = "tplinfo";

        public SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        public SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
        public SwiftMetaDataColumn tId = new MetaDataColumnBean("tId", Types.VARCHAR);
        public SwiftMetaDataColumn tName = new MetaDataColumnBean("tName", Types.VARCHAR);
        public SwiftMetaDataColumn condition = new MetaDataColumnBean("condition", Types.BIGINT);
        public SwiftMetaDataColumn formula = new MetaDataColumnBean("formula", Types.BIGINT);
        public SwiftMetaDataColumn sheet = new MetaDataColumnBean("sheet", Types.BIGINT);
        public SwiftMetaDataColumn ds = new MetaDataColumnBean("ds", Types.BIGINT);
        public SwiftMetaDataColumn complexFormula = new MetaDataColumnBean("complexFormula", Types.BIGINT);
        public SwiftMetaDataColumn submission = new MetaDataColumnBean("submission", Types.BIGINT);
        public SwiftMetaDataColumn frozen = new MetaDataColumnBean("frozen", Types.BIGINT);
        public SwiftMetaDataColumn foldTree = new MetaDataColumnBean("foldTree", Types.BIGINT);
        public SwiftMetaDataColumn widget = new MetaDataColumnBean("widget", Types.BIGINT);
        public SwiftMetaDataColumn templateSize = new MetaDataColumnBean("templateSize", Types.BIGINT);
        public SwiftMetaDataColumn imageSize = new MetaDataColumnBean("imageSize", Types.BIGINT);
        public SwiftMetaDataColumn execution0 = new MetaDataColumnBean("execution0", Types.BIGINT);
        public SwiftMetaDataColumn execution1 = new MetaDataColumnBean("execution1", Types.BIGINT);
        public SwiftMetaDataColumn execution2 = new MetaDataColumnBean("execution2", Types.BIGINT);
        public SwiftMetaDataColumn execution3 = new MetaDataColumnBean("execution3", Types.BIGINT);
        public SwiftMetaDataColumn execution4 = new MetaDataColumnBean("execution4", Types.BIGINT);
        public SwiftMetaDataColumn memory0 = new MetaDataColumnBean("memory0", Types.BIGINT);
        public SwiftMetaDataColumn memory1 = new MetaDataColumnBean("memory1", Types.BIGINT);
        public SwiftMetaDataColumn memory2 = new MetaDataColumnBean("memory2", Types.BIGINT);
        public SwiftMetaDataColumn memory3 = new MetaDataColumnBean("memory3", Types.BIGINT);
        public SwiftMetaDataColumn memory4 = new MetaDataColumnBean("memory4", Types.BIGINT);
        public SwiftMetaDataColumn sql0 = new MetaDataColumnBean("sql0", Types.BIGINT);
        public SwiftMetaDataColumn sql1 = new MetaDataColumnBean("sql1", Types.BIGINT);
        public SwiftMetaDataColumn sql2 = new MetaDataColumnBean("sql2", Types.BIGINT);
        public SwiftMetaDataColumn sql3 = new MetaDataColumnBean("sql3", Types.BIGINT);
        public SwiftMetaDataColumn sql4 = new MetaDataColumnBean("sql4", Types.BIGINT);
        public SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        public SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, id, tId, tName, condition, formula, sheet, ds, complexFormula, submission, frozen, foldTree,
                    widget, templateSize, imageSize, execution0, execution1, execution2, execution3, execution4,
                    memory0, memory1, memory2, memory3, memory4, sql0, sql1, sql2, sql3, sql4, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    private static class WebContainer {
        public static final String tableName = "web_container";
        public static final String fileName = "webcontainer";

        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn containerType = new MetaDataColumnBean("containerType", Types.VARCHAR);
        private SwiftMetaDataColumn containerMemory = new MetaDataColumnBean("containerMemory", Types.BIGINT);
        private SwiftMetaDataColumn cpu = new MetaDataColumnBean("cpu", Types.BIGINT);
        private SwiftMetaDataColumn disk = new MetaDataColumnBean("disk", Types.VARCHAR);
        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, containerType, containerMemory, cpu, disk, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }
}
