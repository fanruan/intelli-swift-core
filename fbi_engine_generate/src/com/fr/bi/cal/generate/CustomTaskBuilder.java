package com.fr.bi.cal.generate;

import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.utils.CubeUpdateUtils;
import com.fr.bi.cal.generate.task.AllCubeGenerateTask;
import com.fr.bi.cal.generate.task.EmptyCubeGenerateTask;
import com.finebi.cube.conf.ICubeGenerateTask;
import com.fr.bi.cal.generate.task.PartCubeGenerateTask;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.general.ComparatorUtils;

import java.util.*;

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
     * @param userId
     * @return
     */
    private static boolean isUpdateMeta(long userId) {
        /**
         * 关联减少，表没有变化
         */
        boolean relationReduced = BICubeConfigureCenter.getTableRelationManager().isRelationReduced(userId) &&
                BICubeConfigureCenter.getPackageManager().isTableNoChange(userId);
        /**
         * 表减少，关联没有变化
         */
        boolean tableReduced = BICubeConfigureCenter.getPackageManager().isTableReduced(userId) &&
                BICubeConfigureCenter.getTableRelationManager().isRelationNoChange(userId);
        /**
         * 关联和表都减少了
         */
        boolean tableRelationReduced = BICubeConfigureCenter.getPackageManager().isTableReduced(userId) &&
                BICubeConfigureCenter.getTableRelationManager().isRelationReduced(userId);
        return relationReduced || tableReduced || tableRelationReduced;
    }

    private static boolean isPart(long userId) {
        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(Long.toString(userId)));
        Cube cube = new BICube(resourceRetrievalService, discovery);
        boolean isPart = (CubeUpdateUtils.getCubeAbsentTables(userId).size() > 0 || CubeUpdateUtils.getCubeAbsentRelations(userId).size() > 0 || CubeUpdateUtils.getCubeAbsentPaths(userId).size() > 0) && cube.isVersionAvailable();
        return isPart;
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
        if (isPart(userId)) {
            return new PartCubeGenerateTask(userId);
        } else if (isUpdateMeta(userId)) {
            return new EmptyCubeGenerateTask(userId);
        } else {
            return new AllCubeGenerateTask(userId);
        }
    }
}
