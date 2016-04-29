package com.fr.bi.conf.log;

import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.json.JSONCreator;

import java.util.Date;
import java.util.Set;

/**
 * Created by FineSoft on 2015/5/28.
 */
public interface BIRecord extends JSONCreator {

    /**
     * 开始执行日志
     */
    public void recordStart();

    /**
     * 关联日志开始
     */
    public void recordRelationStart();

    /**
     * 日志结束
     */
    public void recordEnd();

    /**
     * 日志开始
     */
    public void recordIndexStart();

    /**
     * 表错误日志
     *
     * @param table 表
     * @param text  内容
     */
    public void recordToErrorTable(Table table, String text);

    /**
     * 表日志
     *  @param table   表
     * @param seconds       参数
     * @param percent 百分比
     */
    public void recordToInfoTable(Table table, long seconds, int percent);

    /**
     * 表日志
     *  @param table   表
     * @param seconds       参数
     * @param percent 百分比
     */
    public void readingInfoTable(Table table, long seconds, int percent);

    /**
     * 表读日志
     *  @param table 表
     * @param seconds     参数
     */
    public void readingInfoTable(Table table, long seconds);

    /**
     * 表日志
     *  @param table 表
     * @param seconds     参数
     */
    public void recordToInfoTable(Table table, long seconds);

    /**
     * 表日志
     *  @param table 表
     * @param seconds     参数
     */
    public void recordIndexToInfoTable(Table table, long seconds);

    /**
     * 表日志
     *  @param table      表
     * @param columnName 列名
     * @param seconds          参数
     * @param percent    百分比
     */
    public void recordColumnToInfoTable(Table table, String columnName, long seconds, int percent);

    /**
     * 表日志
     *  @param table      表
     * @param columnName 列名
     * @param seconds          参数
     */
    public void recordColumnToInfoTable(Table table, String columnName, long seconds);

    /**
     * 错误日志
     *  @param ck   列关键字
     * @param text 内容
     */
    public void errorRelation(RelationColumnKey ck, String text);

    /**
     * 错误日志
     * @param ck      列关键字
     * @param seconds       参数
     * @param percent 百分比
     */
    public void infoRelation(RelationColumnKey ck, long seconds, int percent);

    /**
     * 错误日志
     *  @param ck 列关键字
     * @param seconds  参数*/
    public void infoRelation(RelationColumnKey ck, long seconds);

    /**
     * 死循环错误日志
     */
    public void loopRelation(Set<ArrayKey<BITableSourceRelation>> set);

    public Date getEnd();
}
