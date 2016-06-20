package com.fr.bi.conf.log;

import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.data.source.CubeTableSource;
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
     void recordStart();

    /**
     * 关联日志开始
     */
     void recordRelationStart();

    /**
     * 日志结束
     */
     void recordEnd();

    /**
     * 数据版本变化
     */
    void recordVersion();

    /**
     * 日志开始
     */
     void recordIndexStart();

    /**
     * 表错误日志
     *  @param table 表
     * @param text  内容
     */
     void recordToErrorTable(IPersistentTable table, String text);

    /**
     * 表日志
     * @param table   表
     * @param seconds       参数
     * @param percent 百分比
     */
     void recordToInfoTable(IPersistentTable table, long seconds, int percent);

    /**
     * 表日志
     * @param table   表
     * @param seconds       参数
     * @param percent 百分比
     */
     void readingInfoTable(IPersistentTable table, long seconds, int percent);

    /**
     * 表读日志
     * @param table 表
     * @param seconds     参数
     */
     void readingInfoTable(IPersistentTable table, long seconds);

    /**
     * 表日志
     * @param table 表
     * @param seconds     参数
     */
     void recordToInfoTable(IPersistentTable table, long seconds);

    /**
     * 表日志
     * @param table 表
     * @param seconds     参数
     */
     void recordIndexToInfoTable(IPersistentTable table, long seconds);

    /**
     * 表日志
     * @param table      表
     * @param columnName 列名
     * @param seconds          参数
     * @param percent    百分比
     */
     void recordColumnToInfoTable(IPersistentTable table, String columnName, long seconds, int percent);

    /**
     * 表日志
     * @param table      表
     * @param columnName 列名
     * @param seconds          参数
     */
     void recordColumnToInfoTable(IPersistentTable table, String columnName, long seconds);

    /**
     * 错误日志
     *  @param ck   列关键字
     * @param text 内容
     */
     void errorRelation(RelationColumnKey ck, String text);

    /**
     * 错误日志
     * @param ck      列关键字
     * @param seconds       参数
     * @param percent 百分比
     */
     void infoRelation(RelationColumnKey ck, long seconds, int percent);

    /**
     * 错误日志
     *  @param ck 列关键字
     * @param seconds  参数*/
     void infoRelation(RelationColumnKey ck, long seconds);

    /**
     * 死循环错误日志
     */
     void loopRelation(Set<ArrayKey<BITableSourceRelation>> set);

     Date getConfigVersion();

    /**
     * 获取所有需要更新的relation信息
     * @param biTableSourceRelationHashSet
     */
    void reLationSet(Set<BITableSourceRelationPath> biTableSourceRelationHashSet);

    /**
     * 获取所有需要更新的tableSource信息
     */
    void cubeTableSourceSet(Set<CubeTableSource> cubeTableSources);
}
