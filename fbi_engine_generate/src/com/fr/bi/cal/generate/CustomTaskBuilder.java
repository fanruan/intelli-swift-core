package com.fr.bi.cal.generate;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.ICubeGenerateTask;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.utils.CubeUpdateUtils;
import com.fr.bi.cal.generate.task.AllCubeGenerateTask;
import com.fr.bi.cal.generate.task.EmptyCubeGenerateTask;
import com.fr.bi.cal.generate.task.PartCubeGenerateTask;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Lucifer on 2017-4-1.
 * 单表更新任务合并及构建
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomTaskBuilder {


    /**
     * 检查ETL表的基础表，是否存在或在本次更新基础表内
     * 是则更新，否则不更新。
     *
     * @param userId
     * @param etlTableSource
     * @param baseSourceIds
     * @return
     */
    private static boolean checkETLTable(long userId, ETLTableSource etlTableSource, Set<String> baseSourceIds) {
        List<Set<CubeTableSource>> layers = etlTableSource.createGenerateTablesList();
        for (CubeTableSource tableSource : CubeUpdateUtils.toSet(layers)) {
            if (TableSourceUtils.isBasicTable(tableSource)) {
                if (!CubeUpdateUtils.isTableExist(userId, tableSource) && !baseSourceIds.contains(tableSource.getSourceID())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 前置条件检查（检查空间是否足够）
     *
     * @param userId
     * @param cubeBuildStuff
     * @return
     */
    private boolean preConditionsCheck(long userId, CubeBuildStuff cubeBuildStuff) {
        boolean conditionsMeet = cubeBuildStuff.preConditionsCheck();
        if (!conditionsMeet) {
            String errorMessage = "preConditions check failed!";
            BILoggerFactory.getLogger(this.getClass()).error(errorMessage);
        }
        return conditionsMeet;
    }


    /**
     * 根据baseTableSourceId获取所有相关的businessTable
     *
     * @param userId
     * @param baseTableSourceId
     * @return
     */
    public static List<BusinessTable> getBusinessTablesContainsSourceId(long userId, String baseTableSourceId) {
        List<BusinessTable> tableList = new ArrayList<BusinessTable>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            List<Set<CubeTableSource>> sourceList = businessTable.getTableSource().createGenerateTablesList();
            for (Set<CubeTableSource> sourceSet : sourceList) {
                Iterator iterator = sourceSet.iterator();
                while (iterator.hasNext()) {
                    CubeTableSource tableSource = (CubeTableSource) iterator.next();
                    if (ComparatorUtils.equals(tableSource.getSourceID(), baseTableSourceId)) {
                        tableList.add(businessTable);
                    }
                }
            }
        }
        return tableList;
    }

    /**
     * 判断etl表的基础表是否都存在或在本次更新的基础表中，是则更新，不是则丢弃。
     *
     * @param tableList
     * @param userId
     * @param baseSourceIds
     * @return
     */
    public static List<CubeTableSource> getTableSourcesNeedUpdate(List<BusinessTable> tableList, long userId, Set<String> baseSourceIds) {
        List<CubeTableSource> tableSources = new ArrayList<CubeTableSource>();
        for (BusinessTable table : tableList) {
            if (table.getTableSource() instanceof ETLTableSource) {
                if (checkETLTable(userId, (ETLTableSource) table.getTableSource(), baseSourceIds)) {
                    tableSources.add(table.getTableSource());
                }
            } else {
                tableSources.add(table.getTableSource());
            }
        }
        return removeDuplicateSources(tableSources);
    }

    private static List<CubeTableSource> removeDuplicateSources(List<CubeTableSource> sourceList) {
        List<CubeTableSource> tableSources = new ArrayList<CubeTableSource>();
        Set<String> sourceIds = new HashSet<String>();
        for (CubeTableSource source : sourceList) {
            if (!sourceIds.contains(source.getSourceID())) {
                sourceIds.add(source.getSourceID());
                tableSources.add(source);
            }
        }
        return tableSources;
    }

    public static ICubeGenerateTask getCubeGenerateTask(long userId) {
        if (CubeUpdateUtils.isPart(userId)) {
            return new PartCubeGenerateTask(userId);
        } else if (CubeUpdateUtils.isUpdateMeta(userId)) {
            return new EmptyCubeGenerateTask(userId);
        } else {
            return new AllCubeGenerateTask(userId);
        }
    }
}
