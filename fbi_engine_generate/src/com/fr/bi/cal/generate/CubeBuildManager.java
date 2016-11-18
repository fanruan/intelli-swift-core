package com.fr.bi.cal.generate;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.*;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.impl.conf.CubeBuildStuffComplete;
import com.finebi.cube.impl.conf.CubeBuildStuffSpecificTable;
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
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.general.ComparatorUtils;

import java.util.*;

/**
 * Created by kary on 16/5/30.
 */
public class CubeBuildManager {

    private BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();

    public boolean CubeBuildSingleTable(long userId, String baseTableSourceId, int updateType) {
        List<CubeBuildStuff> cubeBuildList = buildSingleTable(userId, baseTableSourceId, updateType);
        boolean taskAdd = true;
        for (CubeBuildStuff cubeBuild : cubeBuildList) {
            taskAdd = cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuild), userId) && taskAdd;
        }
        return taskAdd;
    }

    public List<CubeBuildStuff> buildSingleTable(long userId, String baseTableSourceId, int updateType) {
        BILoggerFactory.getLogger().info(BIDateUtils.getCurrentDateTime() + " Cube single table update start");
        List<CubeBuildStuff> cubeBuildStuffList = new ArrayList<CubeBuildStuff>();
        List<BusinessTable> tableList = getTablesContainsSourceId(userId, baseTableSourceId, updateType);
        List<CubeTableSource> tableSourceList = getTableSourcesFromBusinessTables(tableList, userId, baseTableSourceId);
        for (CubeTableSource tableSource : tableSourceList) {
            cubeBuildStuffList.add(getCubeBuildStuffSpecificTable(userId, tableSource, baseTableSourceId, updateType));
        }
        return cubeBuildStuffList;
    }

    private CubeBuildStuff getCubeBuildStuffSpecificTable(long userId, CubeTableSource cubeTableSource, String baseTableSourceId, int updateType) {
        return new CubeBuildStuffSpecificTable(
                userId,
                cubeTableSource,
                baseTableSourceId,
                updateType,
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

    public boolean CubeBuildStaff(long userId) {
        boolean taskAddResult = false;
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
        } else {
            msg.append(" Cube all update start");
            cubeBuild = new CubeBuildStuffComplete(new BIUser(userId));
            BILoggerFactory.getLogger().info(BIDateUtils.getCurrentDateTime() + " preCondition checking……");
        }
        if (preConditionsCheck(userId, cubeBuild)) {
            CubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuild);
            BILoggerFactory.getLogger().info(BIDateUtils.getCurrentDateTime() + msg);
            taskAddResult = cubeManager.addTask(task, userId);
        }
        return taskAddResult;
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
            String errorMessage = "preConditions check failed! Please check the available HD space and data connections";
            BILoggerFactory.getLogger().error(errorMessage);
            BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), errorMessage, userId);
            BIConfigureManagerCenter.getLogManager().logEnd(userId);
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
