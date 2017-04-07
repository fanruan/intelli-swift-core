package com.fr.bi.cal.generate;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.impl.conf.CubeBuildCustomTables;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.finebi.cube.impl.conf.CubeBuildStuffEmptyTable;
import com.finebi.cube.impl.conf.CubeBuildStuffSupplement;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.utils.CubeUpdateUtils;
import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.data.source.TableSourceUtils;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.bi.stable.utils.time.BIDateUtils;
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

    public CustomTaskBuilder() {
    }

    /**
     * 自定义表更新
     *
     * @param userId
     * @param baseTableSourceIds
     * @param updateTypes
     */
    public List<CubeBuildStuff> CubeBuildCustomTables(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes) {
        BILoggerFactory.getLogger().info("Update tables ID:" + baseTableSourceIds.toArray());
        return buildCustomTable(userId, baseTableSourceIds, updateTypes);
    }

    public List<CubeBuildStuff> buildCustomTable(long userId, List<String> baseTableSourceIds, List<Integer> updateTypes) {
        List<CubeBuildStuff> cubeBuildStuffList = new ArrayList<CubeBuildStuff>();

        Map<CubeTableSource, Set<String>> tableBaseSourceIdMap = new HashMap<CubeTableSource, Set<String>>();
        Map<String, Set<Integer>> baseSourceIdUpdateTypeMap = new HashMap<String, Set<Integer>>();

        for (int i = 0; i < baseTableSourceIds.size(); i++) {
            List<BusinessTable> tableList = getTablesContainsSourceId(userId, baseTableSourceIds.get(i), updateTypes.get(i));
            List<CubeTableSource> tableSourceList = getTableSourcesFromBusinessTables(tableList, userId, baseTableSourceIds.get(i));
            for (CubeTableSource tableSource : tableSourceList) {
                if (tableBaseSourceIdMap.containsKey(tableSource)) {
                    tableBaseSourceIdMap.get(tableSource).add(baseTableSourceIds.get(i));
                } else {
                    tableBaseSourceIdMap.put(tableSource, new HashSet<String>());
                    tableBaseSourceIdMap.get(tableSource).add(baseTableSourceIds.get(i));
                }

                if (baseSourceIdUpdateTypeMap.containsKey(baseTableSourceIds.get(i))) {
                    baseSourceIdUpdateTypeMap.get(baseTableSourceIds.get(i)).add(updateTypes.get(i));
                } else {
                    baseSourceIdUpdateTypeMap.put(baseTableSourceIds.get(i), new HashSet<Integer>());
                    baseSourceIdUpdateTypeMap.get(baseTableSourceIds.get(i)).add(updateTypes.get(i));
                }

            }

        }

        cubeBuildStuffList.add(getCubeBuildStuffCustomTables(userId, tableBaseSourceIdMap, baseSourceIdUpdateTypeMap));
        return cubeBuildStuffList;
    }

    private CubeBuildStuff getCubeBuildStuffCustomTables(long userId, Map<CubeTableSource, Set<String>> tableBaseSourceIdMap,
                                                         Map<String, Set<Integer>> baseSourceIdUpdateTypeMap) {
        return new CubeBuildCustomTables(
                userId,
                tableBaseSourceIdMap,
                baseSourceIdUpdateTypeMap,
                getAbsentTable(userId),
                getAbsentRelation(userId),
                getAbsentPath(userId));
    }


    /**
     * 检查ETL表的基础表，是否存在。
     *
     * @param userId
     * @param etlTableSource
     * @param basicTableID
     * @return
     */
    private boolean checkETLTable(long userId, ETLTableSource etlTableSource, String basicTableID) {
        List<Set<CubeTableSource>> layers = etlTableSource.createGenerateTablesList();
        for (CubeTableSource tableSource : CubeUpdateUtils.toSet(layers)) {
            if (TableSourceUtils.isBasicTable(tableSource)) {
                if (!CubeUpdateUtils.isTableExist(userId, tableSource) &&
                        !ComparatorUtils.equals(tableSource.getSourceID(), basicTableID)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 直接执行全局更新，不会进行check
     *
     * @param userId
     */
    public CubeTask buildCompleteStuff(long userId) {
        BILoggerFactory.getLogger(this.getClass()).info(BIStringUtils.append(BIDateUtils.getCurrentDateTime(), " Cube all update start"));
        CubeBuildStuff cubeBuild = new CubeBuildStuffComplete(new BIUser(userId));
        CubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuild);
        return task;
    }

    public CubeTask buildStaff(long userId) {
        CubeBuildStuff cubeBuild;
        /**
         * 若cube不存在,全局更新
         * 若有新增表或者新增关联，增量更新，否则进行全量
         */
        StringBuffer msg = new StringBuffer();
        if (isPart(userId)) {
            msg.append(" Cube part update start" + "\n");
            Set<CubeTableSource> absentTables = getAbsentTable(userId);
            Set<BITableSourceRelation> absentRelations = getAbsentRelation(userId);
            Set<BITableSourceRelationPath> absentPaths = getAbsentPath(userId);
            cubeBuild = new CubeBuildStuffSupplement(userId, absentTables, absentRelations, absentPaths);
        } else if (isUpdateMeta(userId)) {
            msg.append(" Cube update meta data");
            cubeBuild = new CubeBuildStuffEmptyTable(userId);
        } else {
            msg.append(" Cube all update start");
            cubeBuild = new CubeBuildStuffComplete(new BIUser(userId));
            BILoggerFactory.getLogger().info(BIDateUtils.getCurrentDateTime() + " preCondition checking……");
        }
        CubeTask task = null;
        if (preConditionsCheck(userId, cubeBuild)) {
            task = new BuildCubeTask(new BIUser(userId), cubeBuild);
            BILoggerFactory.getLogger().info(BIDateUtils.getCurrentDateTime() + msg);
        }
        return task;
    }

    /**
     * @param userId
     * @return
     */
    private boolean isUpdateMeta(long userId) {
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

    private Set<CubeTableSource> getAbsentTable(long userId) {
        return CubeUpdateUtils.getCubeAbsentTables(userId);
    }

    private Set<BITableSourceRelation> getAbsentRelation(long userId) {
        return CubeUpdateUtils.getCubeAbsentRelations(userId);
    }

    private Set<BITableSourceRelationPath> getAbsentPath(long userId) {
        return CubeUpdateUtils.getCubeAbsentPaths(userId);

    }

    private boolean isPart(long userId) {
        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(Long.toString(userId)));
        Cube cube = new BICube(resourceRetrievalService, discovery);
        boolean isPart = (CubeUpdateUtils.getCubeAbsentTables(userId).size() > 0 || CubeUpdateUtils.getCubeAbsentRelations(userId).size() > 0 || CubeUpdateUtils.getCubeAbsentPaths(userId).size() > 0) && cube.isVersionAvailable();
        return isPart;
    }

    private boolean preConditionsCheck(long userId, CubeBuildStuff cubeBuild) {
        boolean conditionsMeet = cubeBuild.preConditionsCheck();
        if (!conditionsMeet) {
            String errorMessage = "preConditions check failed!";
            BILoggerFactory.getLogger(this.getClass()).error(errorMessage);
        }
        return conditionsMeet;
    }

    private List<BusinessTable> getTablesContainsSourceId(long userId, String baseTableSourceId, int updateType) {
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

    private List<CubeTableSource> getTableSourcesFromBusinessTables(List<BusinessTable> tableList, long userId, String baseTableSourceId) {
        List<CubeTableSource> tableSources = new ArrayList<CubeTableSource>();
        for (BusinessTable table : tableList) {
            if (table.getTableSource() instanceof ETLTableSource) {
                if (checkETLTable(userId, (ETLTableSource) table.getTableSource(), baseTableSourceId)) {
                    tableSources.add(table.getTableSource());
                }
            } else {
                tableSources.add(table.getTableSource());
            }
        }
        return removeDuplicateSources(tableSources);
    }

    private List<CubeTableSource> removeDuplicateSources(List<CubeTableSource> sourceList) {
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
}
