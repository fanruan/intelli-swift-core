package com.fr.bi.conf.log;


import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.IPersistentTable;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.general.GeneralContext;
import com.fr.json.JSONObject;
import com.fr.stable.EnvChangedListener;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
//tod kary 现在只能监听cube生成过程中产生的异常，后期可考虑监听所有和cube相关的异常
public class BILogManager implements BILogManagerProvider {


    private static Map<Long, SingleUserBIRecord> userMap = new ConcurrentHashMap<Long, SingleUserBIRecord>();

    protected static void envChanged() {
        userMap.clear();
    }

    private BIRecord getInstance(long userId) {
        return BIConstructorUtils.constructObject(userId, SingleUserBIRecord.class, userMap);
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            @Override
            public void envChanged() {
                BILogManager.envChanged();
            }
        });
    }

    /**
     * 开始执行日志
     */
    @Override
    public void logStart(long userId) {
        getInstance(userId).recordStart();
    }

    /**
     * 关联日志开始
     */
    @Override
    public void logRelationStart(long userId) {
        getInstance(userId).recordRelationStart();
    }

    /**
     * 日志结束
     */
    @Override
    public void logEnd(long userId) {
        getInstance(userId).recordEnd();
    }

    /**
     * 日志开始
     */
    @Override
    public void logIndexStart(long userId) {
        getInstance(userId).recordIndexStart();
    }

    /**
     * 表错误日志
     *  @param table 表
     * @param text  内容
     */
    @Override
    public void errorTable(IPersistentTable table, String text, long userId) {
        getInstance(userId).recordToErrorTable(table, text);
    }

    /**
     * 表日志
     * @param table   表
     * @param seconds  时间(秒)
     * @param percent 百分比
     */
    @Override
    public void infoTable(IPersistentTable table, long seconds, int percent, long userId) {
        getInstance(userId).recordToInfoTable(table, seconds, percent);
    }

    /**
     * 表日志
     * @param table   表
     * @param seconds  时间(秒)
     * @param percent 百分比
     */
    @Override
    public void infoTableReading(IPersistentTable table, long seconds, int percent, long userId) {
        getInstance(userId).readingInfoTable(table, percent);
    }

    /**
     * 表读日志
     * @param table 表
     * @param seconds  时间(秒)
     */
    @Override
    public void infoTableReading(IPersistentTable table, long seconds, long userId) {
        getInstance(userId).readingInfoTable(table, seconds);
    }

    /**
     * 表日志
     * @param table 表
     * @param seconds 时间(秒)
     */
    @Override
    public void infoTable(IPersistentTable table, long seconds, long userId) {
        getInstance(userId).recordToInfoTable(table, seconds);
    }

    /**
     * 表日志
     * @param table 表
     * @param seconds 时间(秒)
     */
    @Override
    public void infoTableIndex(IPersistentTable table, long seconds, long userId) {
        getInstance(userId).recordIndexToInfoTable(table, seconds);

    }

    /**
     * 表日志
     * @param table      表
     * @param columnName 列名
     * @param seconds    时间(秒)
     * @param percent    百分比
     */
    @Override
    public void infoColumn(IPersistentTable table, String columnName, long seconds, int percent, long userId) {
        getInstance(userId).recordColumnToInfoTable(table, columnName, seconds, percent);
    }

    /**
     * 表日志
     * @param table      表
     * @param columnName 列名
     * @param seconds    时间(秒)
     */
    @Override
    public void infoColumn(IPersistentTable table, String columnName, long seconds, long userId) {
        getInstance(userId).recordColumnToInfoTable(table, columnName, seconds);
    }

    /**
     * 错误日志
     *  @param ck   列关键字
     * @param text 内容
     */
    @Override
    public void errorRelation(RelationColumnKey ck, String text, long userId) {
        getInstance(userId).errorRelation(ck, text);
    }

    /**
     * 错误日志
     * @param ck      列关键字
     * @param seconds  时间(秒)
     * @param percent 百分比
     */
    @Override
    public void infoRelation(RelationColumnKey ck, long seconds, int percent, long userId) {
        getInstance(userId).infoRelation(ck, seconds, percent);
    }

    /**
     * 错误日志
     *  @param ck 列关键字
     * @param seconds  时间(秒)
     */
    @Override
    public void infoRelation(RelationColumnKey ck, long seconds, long userId) {
        getInstance(userId).infoRelation(ck, seconds);
    }

    /**
     * 死循环错误日志
     *
     * @param set map对象
     */
    @Override
    public void loopRelation(Set<ArrayKey<BITableSourceRelation>> set, long userId) {
        getInstance(userId).loopRelation(set);
    }

    /**
     * 创建JSON
     *
     * @return jsonObject对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON(long userId) throws Exception {
        return getInstance(userId).createJSON();
    }

    @Override
    public BIRecord getBILog(long userId) {
        return getInstance(userId);
    }

    @Override
    public Date getCubeEnd(long userId) {
        return getInstance(userId).getEnd();
    }


}
