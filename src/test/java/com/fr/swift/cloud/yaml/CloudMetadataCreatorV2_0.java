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
 * @description 生成v2.0 metadata的yaml文件
 */
public class CloudMetadataCreatorV2_0 {

    public static void main(String[] args) throws Exception {
        Map<String, SwiftMetaData> map = new LinkedHashMap<>();
        map.put(Execution.fileName, new Execution().getMetadata());
        map.put(ExecutionSql.fileName, new ExecutionSql().getMetadata());
        map.put(FunctionUsageRate.fileName, new FunctionUsageRate().getMetadata());
        map.put(PackageInfo.fileName, new PackageInfo().getMetadata());
        map.put(RealTimeUsage.fileName, new RealTimeUsage().getMetadata());
        map.put(TemplateInfo.fileName, new TemplateInfo().getMetadata());

        map.put(ConfEntity.fileName, new ConfEntity().getMetadata());
        map.put(ConsumePoint.fileName, new ConsumePoint().getMetadata());
        map.put(ContainerMessage.fileName, new ContainerMessage().getMetadata());
        map.put(FunctionPossess.fileName, new FunctionPossess().getMetadata());
        map.put(PluginUsage.fileName, new PluginUsage().getMetadata());
        map.put(ShutdownRecord.fileName, new ShutdownRecord().getMetadata());

        map.put(GcRecord.fileName, new GcRecord().getMetadata());

        Yaml.dump(map, new File("cloud.v2.0.yaml"));
        Map<String, SwiftMetaData> metaDataMap = Yaml.loadType(new File("cloud.v2.0.yaml"), HashMap.class);
        Map<String, SwiftMetaData> metaDataMap1 = CloudVersionProperty.getProperty().getMetadataMapByVersion("2.0");
    }

    public static class GcRecord {
        public static final String tableName = "gc_record";
        public static final String fileName = "fanruan.gc.log";

        public SwiftMetaDataColumn gcStartTime = new MetaDataColumnBean("gcStartTime", Types.BIGINT);
        public SwiftMetaDataColumn gcType = new MetaDataColumnBean("gcType", Types.VARCHAR);
        public SwiftMetaDataColumn pid = new MetaDataColumnBean("pid", Types.BIGINT);
        public SwiftMetaDataColumn duration = new MetaDataColumnBean("duration", Types.BIGINT);

        //暂时先按一行为一个字段存储
        public SwiftMetaDataColumn youngBefore = new MetaDataColumnBean("youngBefore", Types.BIGINT);
        public SwiftMetaDataColumn youngAfter = new MetaDataColumnBean("youngAfter", Types.BIGINT);
        public SwiftMetaDataColumn youngTotal = new MetaDataColumnBean("youngTotal", Types.BIGINT);

        public SwiftMetaDataColumn oldBefore = new MetaDataColumnBean("oldBefore", Types.BIGINT);
        public SwiftMetaDataColumn oldAfter = new MetaDataColumnBean("oldAfter", Types.BIGINT);
        public SwiftMetaDataColumn oldTotal = new MetaDataColumnBean("oldTotal", Types.BIGINT);

        public SwiftMetaDataColumn metaspaceBefore = new MetaDataColumnBean("metaspaceBefore", Types.BIGINT);
        public SwiftMetaDataColumn metaspaceAfter = new MetaDataColumnBean("metaspaceAfter", Types.BIGINT);
        public SwiftMetaDataColumn metaspaceTotal = new MetaDataColumnBean("metaspaceTotal", Types.BIGINT);

        public SwiftMetaDataColumn sumBefore = new MetaDataColumnBean("sumBefore", Types.BIGINT);
        public SwiftMetaDataColumn sumAfter = new MetaDataColumnBean("sumAfter", Types.BIGINT);
        public SwiftMetaDataColumn sumTotal = new MetaDataColumnBean("sumTotal", Types.BIGINT);

        public SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        public SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(gcStartTime, gcType, pid, duration, youngBefore, youngAfter, youngTotal, oldBefore, oldAfter, oldTotal,
                    metaspaceBefore, metaspaceAfter, metaspaceTotal, sumBefore, sumAfter, sumTotal, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }


    /**
     * package
     */
    public static class PackageInfo {

        public static final String tableName = "package_info";
        public static final String fileName = "package";

        private SwiftMetaDataColumn appName = new MetaDataColumnBean("appName", Types.VARCHAR);
        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn version = new MetaDataColumnBean("version", Types.VARCHAR);

        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(appName, appId, time, version, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * containermessage
     */
    private static class ContainerMessage {
        public static final String tableName = "container_message";
        public static final String fileName = "containermessage";

        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn node = new MetaDataColumnBean("node", Types.VARCHAR);
        private SwiftMetaDataColumn item = new MetaDataColumnBean("item", Types.VARCHAR);
        private SwiftMetaDataColumn value = new MetaDataColumnBean("value", Types.VARCHAR);

        public SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        public SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, node, item, value, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * tplinfo
     */
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

        public SwiftMetaDataColumn filternums = new MetaDataColumnBean("filternums", Types.BIGINT);
        public SwiftMetaDataColumn injectnums = new MetaDataColumnBean("injectnums", Types.BIGINT);
        public SwiftMetaDataColumn formulaUsage = new MetaDataColumnBean("formulaUsage", Types.VARCHAR);
        public SwiftMetaDataColumn jsapi = new MetaDataColumnBean("jsapi", Types.VARCHAR);

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
        public SwiftMetaDataColumn recordTime = new MetaDataColumnBean("recordTime", Types.VARCHAR);

        public SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        public SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, id, tId, tName, condition, formula, sheet, ds, complexFormula, submission, frozen, foldTree,
                    widget, filternums, injectnums, formulaUsage, jsapi, templateSize, imageSize, execution0, execution1, execution2, execution3, execution4,
                    memory0, memory1, memory2, memory3, memory4, sql0, sql1, sql2, sql3, sql4, recordTime, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * execute
     */
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
        public SwiftMetaDataColumn userId = new MetaDataColumnBean("userId", Types.VARCHAR);
        public SwiftMetaDataColumn complete = new MetaDataColumnBean("complete", Types.VARCHAR);
        public SwiftMetaDataColumn source = new MetaDataColumnBean("source", Types.VARCHAR);

        public SwiftMetaDataColumn coreConsume = new MetaDataColumnBean("coreConsume", Types.BIGINT);
        public SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        public SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(id, tName, displayName, time, memory, type, consume, sqlTime, reportId, userId, complete, source, coreConsume, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * executesql
     */
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

    /**
     * realtimeusage
     */
    public static class RealTimeUsage {

        public static final String tableName = "real_time_usage";
        public static final String fileName = "realtimeusage";

        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn node = new MetaDataColumnBean("node", Types.VARCHAR);
        private SwiftMetaDataColumn cpu = new MetaDataColumnBean("cpu", Types.DOUBLE);
        private SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
        private SwiftMetaDataColumn sessionnum = new MetaDataColumnBean("sessionnum", Types.BIGINT);
        private SwiftMetaDataColumn onlinenum = new MetaDataColumnBean("onlinenum", Types.BIGINT);
        private SwiftMetaDataColumn pid = new MetaDataColumnBean("pid", Types.BIGINT);

        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, node, cpu, memory, sessionnum, onlinenum, pid, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * functionusagerate
     */
    public static class FunctionUsageRate {

        public static final String tableName = "function_usage_rate";
        public static final String fileName = "functionusagerate";

        private SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn username = new MetaDataColumnBean("username", Types.VARCHAR);
        private SwiftMetaDataColumn source = new MetaDataColumnBean("source", Types.VARCHAR);
        private SwiftMetaDataColumn text = new MetaDataColumnBean("text", Types.VARCHAR);
        private SwiftMetaDataColumn body = new MetaDataColumnBean("body", Types.VARCHAR);

        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(id, time, username, source, text, body, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * shutdownrecord
     */
    private static class ShutdownRecord {
        public static final String tableName = "shutdown_record";
        public static final String fileName = "shutdownrecord";

        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn node = new MetaDataColumnBean("node", Types.VARCHAR);
        private SwiftMetaDataColumn pid = new MetaDataColumnBean("pid", Types.BIGINT);
        private SwiftMetaDataColumn startTime = new MetaDataColumnBean("startTime", Types.DATE);
        private SwiftMetaDataColumn upTime = new MetaDataColumnBean("upTime", Types.BIGINT);
        private SwiftMetaDataColumn signalName = new MetaDataColumnBean("signalName", Types.VARCHAR);
        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, node, pid, startTime, upTime, signalName, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * confentity
     */
    private static class ConfEntity {
        public static final String tableName = "conf_entity";
        public static final String fileName = "confentity";

        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
        private SwiftMetaDataColumn value = new MetaDataColumnBean("value", Types.VARCHAR);

        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, id, value, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * pluginusage
     */
    private static class PluginUsage {
        public static final String tableName = "plugin_usage";
        public static final String fileName = "pluginusage";

        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn plugin = new MetaDataColumnBean("plugin", Types.VARCHAR);
        private SwiftMetaDataColumn version = new MetaDataColumnBean("version", Types.VARCHAR);
        private SwiftMetaDataColumn API = new MetaDataColumnBean("API", Types.VARCHAR);
        private SwiftMetaDataColumn operation = new MetaDataColumnBean("operation", Types.VARCHAR);
        private SwiftMetaDataColumn register = new MetaDataColumnBean("register", Types.VARCHAR);
        private SwiftMetaDataColumn enable = new MetaDataColumnBean("enable", Types.BIGINT);

        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, plugin, version, API, operation, register, enable, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * functionpossess
     */
    private static class FunctionPossess {
        public static final String tableName = "function_possess";
        public static final String fileName = "functionpossess";

        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn function = new MetaDataColumnBean("function", Types.VARCHAR);

        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(time, function, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    /**
     * consumepoint
     */
    private static class ConsumePoint {
        public static final String tableName = "consume_point";
        public static final String fileName = "consumepoint";

        private SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
        private SwiftMetaDataColumn source = new MetaDataColumnBean("source", Types.VARCHAR);
        private SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
        private SwiftMetaDataColumn finish = new MetaDataColumnBean("finish", Types.DATE);
        private SwiftMetaDataColumn consume = new MetaDataColumnBean("consume", Types.BIGINT);
        private SwiftMetaDataColumn memory = new MetaDataColumnBean("memory", Types.BIGINT);
        private SwiftMetaDataColumn title = new MetaDataColumnBean("title", Types.VARCHAR);
        private SwiftMetaDataColumn text = new MetaDataColumnBean("text", Types.VARCHAR);
        private SwiftMetaDataColumn type = new MetaDataColumnBean("type", Types.VARCHAR);
        private SwiftMetaDataColumn comment = new MetaDataColumnBean("comment", Types.VARCHAR);
        private SwiftMetaDataColumn body = new MetaDataColumnBean("body", Types.VARCHAR);
        private SwiftMetaDataColumn username = new MetaDataColumnBean("username", Types.VARCHAR);

        private SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
        private SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

        public SwiftMetaData getMetadata() {
            List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
            columnList.addAll(Arrays.asList(id, source, time, username, finish, consume, memory, title, text, type, comment, body, appId, yearMonth));
            return new SwiftMetaDataBean(tableName, null, tableName, null, columnList);
        }
    }

    private static class GcLog {

    }
}