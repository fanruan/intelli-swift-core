package com.fr.bi.conf.log;


import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

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
    private Set<CubeTableSource> cubeTableSourceSet;
    private Set<BITableSourceRelation> biTableSourceRelationSet;

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
        cubeTableSourceSet=new HashSet<CubeTableSource>();
        biTableSourceRelationSet=new HashSet<BITableSourceRelation>();
    }

    /**
     * 关联日志开始
     */
    @Override
    public void recordRelationStart() {
        if(relation_start==null) {
            relation_start = new Date();
        }
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
        if(index_start==null) {
            index_start = new Date();
        }
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

    /**
     * 获取所有需要更新的relation信息
     *
     * @param biTableSourceRelationHashSet
     */
    @Override
    public void reLationSet(Set<BITableSourceRelation> biTableSourceRelationHashSet) {
        this.biTableSourceRelationSet=biTableSourceRelationHashSet;
    }

    /**
     * 获取所有需要更新的tableSource信息
     *
     * @param cubeTableSources
     */
    @Override
    public void cubeTableSourceSet(Set<CubeTableSource> cubeTableSources) {
        cubeTableSourceSet=cubeTableSources;
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
        JSONObject tableInfo = new JSONObject();
        if (null!=cubeTableSourceSet) {
            for (CubeTableSource cubeTableSource : cubeTableSourceSet) {
                Set<CubeTableSource> tableSourceSet = new HashSet<CubeTableSource>();
                tableSourceSet.add(cubeTableSource);
                tableInfo.put(cubeTableSource.getTableName(), cubeTableSource.getSelfFields(tableSourceSet).size());
            }
        }
        res.put("allRelationInfo",this.biTableSourceRelationSet);
        res.put("allTableInfo",tableInfo);
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
            /**
             * Connery：我一直想我用来生成cube的relation为何被动强制着要一个createjson呢？
             * 改到这里，终于明白该放手。。。。
             * 当这种createJson和parseJson接口，并且有人调用的的时候，整个代码已经
             * 开始出现问题了。当有一个createJson，势必导致属性要有create，以此往复
             * 很快就会蔓延开。parseJson同样如此。
             *
             * 对象的封闭性就此丢失。肆意修改内容，获得内容。如同幽灵一般，到处传递，
             * 只需摇身一变create成为String，传入函数后再parse回来。这样的代码越写越夸张越来越不可控
             *
             * 这类问题造成的bug，一般是矛盾的，也就是会改好A bug，会出现另外一个B bug，改了B bug
             * 然而A bug又出现。但是这类bug往往又是好改，但是改动很多，彻底改完便很难出现。
             *
             *
             * 去除Table的公共父类，已经做了2天了，身心俱疲。这里我特别后悔，当初没有狠下决心
             * 改得彻底。
             * 也就是为了能够限制对象的随便传递，强制使用者来思考。
             * 同时也强制接口提供者来给认真给使用者提供唯一可控的参数选择，而非一个父类
             * 让使用者去猜测应该传一个什么子类，甚至要看源码才能明白需要传递什么。
             *
             * 倘若来人改到这里，恳请细细想想。
             */
//            for (BITableSourceRelation r : re) {
//                ja.put(r.createJSON());
//            }
            loop.put(new JSONObject().put("loops", ja));
        }
    }
}
