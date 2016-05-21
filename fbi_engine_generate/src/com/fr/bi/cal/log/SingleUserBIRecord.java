package com.fr.bi.cal.log;


import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SingleUserBIRecord implements BIRecord {

    public static final String XML_TAG = "bi_log_manager";
    Set<ArrayKey<BITableSourceRelation>> loop_error = new HashSet<ArrayKey<BITableSourceRelation>>();
    private Map<IPersistentTable, BITableLog> tableLogMap = new ConcurrentHashMap<IPersistentTable, BITableLog>();
    private Map<IPersistentTable, BITableLog> tableReadingLogMap = new ConcurrentHashMap<IPersistentTable, BITableLog>();
    private Map<RelationColumnKey, BIConnectionLog> connectionLogMap = new ConcurrentHashMap<RelationColumnKey, BIConnectionLog>();
    private Date cube_start;
    private Date relation_start;
    private Date cube_end;
    private Date index_start;
    private long userId;

    SingleUserBIRecord(long userId) {
        this.userId = userId;
    }

    /**
     * 开始执行日志
     */
    @Override
    public void recordStart() {
        tableLogMap.clear();
        tableReadingLogMap.clear();
        connectionLogMap.clear();
        cube_start = new Date();
        relation_start = null;
        cube_end = null;
        index_start = null;
        loop_error.clear();
    }

    /**
     * 关联日志开始
     */
    @Override
    public void recordRelationStart() {
        relation_start = new Date();
    }

    /**
     * 日志结束
     */
    @Override
    public void recordEnd() {
        cube_end = new Date();
    }

    /**
     * 日志开始
     */
    @Override
    public void recordIndexStart() {
        index_start = new Date();
    }

    /**
     * 表错误日志
     *
     * @param table 表
     * @param text  内容
     */
    @Override
    public void recordToErrorTable(IPersistentTable table, String text) {
        tableLogMap.put(table, new BITableErrorLog(table, text, userId));
    }

    /**
     * 表日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     * @param percent 百分比
     */
    @Override
    public void recordToInfoTable(IPersistentTable table, long seconds, int percent) {
        tableLogMap.put(table, new BITableRunningLog(table, seconds, percent, userId));
    }

    /**
     * 表日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     * @param percent 百分比
     */
    @Override
    public void readingInfoTable(IPersistentTable table, long seconds, int percent) {
        tableReadingLogMap.put(table, new BITableRunningLog(table, seconds, percent, userId));
    }

    /**
     * 表读日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     */
    @Override
    public void readingInfoTable(IPersistentTable table, long seconds) {
        tableReadingLogMap.put(table, new BITableCorrectLog(table, seconds, userId));
    }

    /**
     * 表日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     */
    @Override
    public void recordToInfoTable(IPersistentTable table, long seconds) {
        tableLogMap.put(table, new BITableCorrectLog(table, seconds, userId));
    }

    /**
     * 表日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     */
    @Override
    public void recordIndexToInfoTable(IPersistentTable table, long seconds) {
        BITableLog log = tableLogMap.get(table);
        if (log instanceof BITableCorrectLog) {
            ((BITableCorrectLog) log).setIndexTime(seconds);
        }

    }

    /**
     * 表日志
     *
     * @param table      表
     * @param columnName 列名
     * @param seconds    时间(秒)
     * @param percent    百分比
     */
    @Override
    public void recordColumnToInfoTable(IPersistentTable table, String columnName, long seconds, int percent) {
        BITableLog log = tableLogMap.get(table);
        if (log == null) {
            log = tableReadingLogMap.get(table);
            if (log instanceof BITableCorrectLog) {
                tableLogMap.put(table, new BITableCorrectLog(log.getPersistentTable(), 0, userId));
                log = tableLogMap.get(table);
            }
        }
        if (log instanceof BITableCorrectLog) {
            ((BITableCorrectLog) log).info_column(columnName, seconds, percent);
        }
    }

    /**
     * 表日志
     *
     * @param table      表
     * @param columnName 列名
     * @param seconds    时间(秒)
     */
    @Override
    public void recordColumnToInfoTable(IPersistentTable table, String columnName, long seconds) {
        BITableLog log = tableLogMap.get(table);
        if (log == null) {
            log = tableReadingLogMap.get(table);
            if (log instanceof BITableCorrectLog) {
                tableLogMap.put(table, new BITableCorrectLog(log.getPersistentTable(), 0, userId));
                log = tableLogMap.get(table);
            }
        }
        if (log instanceof BITableCorrectLog) {
            ((BITableCorrectLog) log).info_column(columnName, seconds);
        }
    }

    /**
     * 错误日志
     *
     * @param ck   列关键字
     * @param text 内容
     */
    @Override
    public void errorRelation(RelationColumnKey ck, String text) {
        connectionLogMap.put(ck, new BIConnectionErrorLog(ck, text, userId));
    }

    /**
     * 错误日志
     *
     * @param ck      列关键字
     * @param seconds 时间(秒)
     * @param percent 百分比
     */
    @Override
    public void infoRelation(RelationColumnKey ck, long seconds, int percent) {
        connectionLogMap.put(ck, new BIConnnectionRunningLog(ck, seconds, percent, userId));
    }

    /**
     * 错误日志
     *
     * @param ck      列关键字
     * @param seconds 时间(秒)
     */
    @Override
    public void infoRelation(RelationColumnKey ck, long seconds) {
        connectionLogMap.put(ck, new BIConnectionCorrectLog(ck, seconds, userId));
    }

    /**
     * 死循环错误日志
     *
     * @param set
     */
    @Override
    public void loopRelation(Set<ArrayKey<BITableSourceRelation>> set) {
        synchronized (loop_error) {
            loop_error.addAll(set);
        }
    }

    @Override
    public Date getEnd() {
        return cube_end;
    }

    private void BITableLogSort(List<BITableLog> log) {
        Collections.sort(log, new Comparator<BITableLog>() {
            @Override
            public int compare(BITableLog o1, BITableLog o2) {
                if (o1.isRunning()) {
                    if (o2.isRunning()) {
                        return Long.compare(o1.getTotalTime(), o2.getTotalTime());
                    }
                    return -1;
                } else if (o2.isRunning()) {
                    return 1;
                } else {
                    return Long.compare(o1.getTotalTime(), o2.getTotalTime());
                }
            }
        });
    }

    private void BIConnectionSort(List<BIConnectionLog> log) {
        Collections.sort(log, new Comparator<BIConnectionLog>() {
            @Override
            public int compare(BIConnectionLog o1, BIConnectionLog o2) {
                if (o1.isRunning()) {
                    if (o2.isRunning()) {
                        return Long.compare(o1.getTime(), o2.getTime());
                    }
                    return -1;
                } else if (o2.isRunning()) {
                    return 1;
                } else {
                    return Long.compare(o1.getTime(), o2.getTime());
                }
            }
        });
    }

    private void addTableLog(JSONArray error, List<BITableLog> output) throws Exception {
        Iterator<java.util.Map.Entry<IPersistentTable, BITableLog>> iter = tableLogMap.entrySet().iterator();

        while (iter.hasNext()) {
            java.util.Map.Entry<IPersistentTable, BITableLog> entry = iter.next();
            BITableLog log = entry.getValue();
            if (log instanceof ErrorLog) {
                error.put(log.createJSON());
            } else {
                output.add(log);
            }
        }

    }

    private void addTableReadingLog(List<BITableLog> reading) throws Exception {
        Iterator<java.util.Map.Entry<IPersistentTable, BITableLog>> riter = tableReadingLogMap.entrySet().iterator();
        while (riter.hasNext()) {
            java.util.Map.Entry<IPersistentTable, BITableLog> entry = riter.next();
            BITableLog log = entry.getValue();
            if (tableLogMap.get(entry.getKey()) instanceof BITableErrorLog) {
                continue;
            }
            reading.add(log);
        }

    }

    private void addConnectionLog(JSONArray error, List<BIConnectionLog> coutput) throws Exception {
        Iterator<java.util.Map.Entry<RelationColumnKey, BIConnectionLog>> it = connectionLogMap.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry<RelationColumnKey, BIConnectionLog> entry = it.next();
            BIConnectionLog log = entry.getValue();
            if (log instanceof ErrorLog) {
                error.put(log.createJSON());
            } else {
                coutput.add(log);
            }
        }

    }

    /**
     * 创建JSON
     *
     * @return jsonObject对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject res = new JSONObject();
        JSONArray error = new JSONArray();
        JSONArray loop = new JSONArray();
        JSONArray table_log = new JSONArray();
        JSONArray connection_log = new JSONArray();
        JSONArray reading_log = new JSONArray();
        if (cube_start != null) {
            res.put("cube_start", cube_start.getTime());
        }
        if (relation_start != null) {
            res.put("relation_start", relation_start.getTime());
        }
        if (cube_end != null) {
            res.put("cube_end", cube_end.getTime());
        }
        if (index_start != null) {
            res.put("index_start", index_start.getTime());
        }
        res.put("log_time", System.currentTimeMillis());
        res.put("errors", error);
        res.put("loop_err", loop);
        res.put("tables", table_log);
        res.put("connections", connection_log);
        res.put("readingdb", reading_log);
        dealWithLoopValue(loop);
        List<BITableLog> output = new ArrayList<BITableLog>();
        addTableLog(error, output);
        BITableLogSort(output);
        Iterator<BITableLog> oiter = output.iterator();
        while (oiter.hasNext()) {
            table_log.put(oiter.next().createJSON());
        }
        List<BITableLog> reading = new ArrayList<BITableLog>();
        addTableReadingLog(reading);
        BITableLogSort(reading);
        Iterator<BITableLog> rciter = reading.iterator();
        while (rciter.hasNext()) {
            reading_log.put(rciter.next().createJSON());
        }
        List<BIConnectionLog> coutput = new ArrayList<BIConnectionLog>();
        addConnectionLog(error, coutput);
        BIConnectionSort(coutput);
        Iterator<BIConnectionLog> citer = coutput.iterator();
        while (citer.hasNext()) {
            connection_log.put(citer.next().createJSON());
        }
        return res;
    }


    private void dealWithLoopValue(JSONArray loop) throws Exception {
        for (ArrayKey<BITableSourceRelation> relation : loop_error) {
            BITableSourceRelation[] re = relation.toArray();
            JSONArray ja = new JSONArray();
            for (BITableSourceRelation r : re) {
                ja.put(r.createJSON());
            }
            loop.put(new JSONObject().put("loops", ja));
        }
    }
}