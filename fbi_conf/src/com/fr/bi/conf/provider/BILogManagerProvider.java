package com.fr.bi.conf.provider;

import com.fr.bi.conf.log.BIRecord;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.json.JSONObject;

import java.util.Date;
import java.util.Set;

/**
 * Created by FineSoft on 2015/5/28.
 */
public interface BILogManagerProvider {

    String XML_TAG = "BILogManagerProvider";

    /**
     * 开始执行日志
     */
    public void logStart(long userId);

    /**
     * 关联日志开始
     */
    public void logRelationStart(long userId);

    /**
     * 日志结束
     */
    public void logEnd(long userId);

    /**
     * 日志开始
     */
    public void logIndexStart(long userId);

    /**
     * 表错误日志
     *
     * @param table 表
     * @param text  内容
     */
    public void errorTable(Table table, String text, long userId);

    /**
     * 表日志
     *  @param table   表
     * @param seconds  时间(秒)
     * @param percent 百分比
     */
    public void infoTable(Table table, long seconds, int percent, long userId);

    /**
     * 表日志
     *  @param table   表
     * @param seconds  时间(秒)
     * @param percent 百分比
     */
    public void infoTableReading(Table table, long seconds, int percent, long userId);

    /**
     * 表读日志
     *  @param table    表
     * @param seconds  时间(秒)
     */
    public void infoTableReading(Table table, long seconds, long userId);

    /**
     * 表日志
     *  @param table 表
     * @param seconds 时间(秒)
     */
    public void infoTable(Table table, long seconds, long userId);

    /**
     * 表日志
     *  @param table 表
     * @param seconds 时间(秒)
     */
    public void infoTableIndex(Table table, long seconds, long userId);

    /**
     * 表日志
     *  @param table      表
     * @param columnName 列名
     * @param seconds    时间(秒)
     * @param percent    百分比
     */
    public void infoColumn(Table table, String columnName, long seconds, int percent, long userId);

    /**
     * 表日志
     *  @param table      表
     * @param columnName 列名
     * @param seconds    时间(秒)
     */
    public void infoColumn(Table table, String columnName, long seconds, long userId);

    /**
     * 错误日志
     *  @param ck   列关键字
     * @param text  内容
     */
    public void errorRelation(RelationColumnKey ck, String text, long userId);

    /**
     * 错误日志
     * @param ck      列关键字
     * @param seconds  时间(秒)
     * @param percent 百分比
     */
    public void infoRelation(RelationColumnKey ck, long seconds, int percent, long userId);

    /**
     * 错误日志
     *  @param ck 列关键字
     * @param seconds  时间(秒)
     */
    public void infoRelation(RelationColumnKey ck, long seconds, long userId);

    /**
     * 死循环错误日志
     *
     * @param map map对象
     */
    public void loopRelation(Set<ArrayKey<BITableSourceRelation>> set, long userId);

    public JSONObject createJSON(long userId) throws Exception;

    public BIRecord getBILog(long userId);

    public Date getCubeEnd(long userId);
}
