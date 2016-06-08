package com.fr.bi.conf.provider;

import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.json.JSONObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by FineSoft on 2015/5/28.
 */
public interface BILogManagerProvider {

    String XML_TAG = "BILogManagerProvider";

    /**
     * 开始执行日志
     */
    void logStart(long userId);

    /**
     * 关联日志开始
     */
    void logRelationStart(long userId);

    /**
     * 日志结束
     */
    void logEnd(long userId);

    /**
     * 日志开始
     */
    void logIndexStart(long userId);

    /**
     * 表错误日志
     *
     * @param table 表
     * @param text  内容
     */
    void errorTable(IPersistentTable table, String text, long userId);

    /**
     * 表日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     * @param percent 百分比
     */
    void infoTable(IPersistentTable table, long seconds, int percent, long userId);

    /**
     * 表日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     * @param percent 百分比
     */
    void infoTableReading(IPersistentTable table, long seconds, int percent, long userId);

    /**
     * 表读日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     */
    void infoTableReading(IPersistentTable table, long seconds, long userId);

    /**
     * 表日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     */
    void infoTable(IPersistentTable table, long seconds, long userId);

    /**
     * 表日志
     *
     * @param table   表
     * @param seconds 时间(秒)
     */
    void infoTableIndex(IPersistentTable table, long seconds, long userId);

    /**
     * 表日志
     *
     * @param table      表
     * @param columnName 列名
     * @param seconds    时间(秒)
     * @param percent    百分比
     */
    void infoColumn(IPersistentTable table, String columnName, long seconds, int percent, long userId);

    /**
     * 表日志
     *
     * @param table      表
     * @param columnName 列名
     * @param seconds    时间(秒)
     */
    void infoColumn(IPersistentTable table, String columnName, long seconds, long userId);

    /**
     * 错误日志
     *
     * @param ck   列关键字
     * @param text 内容
     */
    void errorRelation(RelationColumnKey ck, String text, long userId);

    /**
     * 错误日志
     *
     * @param ck      列关键字
     * @param seconds 时间(秒)
     * @param percent 百分比
     */
    void infoRelation(RelationColumnKey ck, long seconds, int percent, long userId);

    /**
     * 错误日志
     *
     * @param ck      列关键字
     * @param seconds 时间(秒)
     */
    void infoRelation(RelationColumnKey ck, long seconds, long userId);

    /**
     * 死循环错误日志
     *
     * @param map map对象
     */
    void loopRelation(Set<ArrayKey<BITableSourceRelation>> set, long userId);

    /**
     * 获取所有需要更新的relation信息
     */
    Set<BITableSourceRelation> reLationSet(Set<BITableSourceRelation> biTableSourceRelationHashSet);

    /**
     * 获取所有需要更新的tableSource信息
     */
    Set<CubeTableSource> cubeTableSourceSet(Set<CubeTableSource> cubeTableSources);

    JSONObject createJSON(long userId) throws Exception;

    BIRecord getBILog(long userId);

    Date getCubeEnd(long userId);
}
