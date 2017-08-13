package com.finebi.cube.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemConfigHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.Cube;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kary on 16/7/14.
 */
public class CubeUpdateUtils {


    private static BILogger logger = BILoggerFactory.getLogger(CubeUpdateUtils.class);

    /**
     * 获得配置部分存在，但是在cube中缺少的表
     * businessTable
     *
     * @param userId 用户ID
     * @return 缺失的表
     */
    public static Set<CubeTableSource> getBusinessCubeAbsentTables(long userId) {
        Set<CubeTableSource> absentTables = new HashSet<CubeTableSource>();
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            CubeTableSource source = businessTable.getTableSource();
            Set<CubeTableSource> tableLayers = toSet(source.createGenerateTablesList());
            for (CubeTableSource layer : tableLayers) {
                if (!BITableKeyUtils.isTableExisted(layer, cubeConfiguration)) {
                    absentTables.add(businessTable.getTableSource());
                    break;
                }
            }
        }
        return absentTables;
    }

    /**
     * 获得配置部分存在，但是在cube中缺少的表，包括etl表层级过程中的表。
     *
     * @param userId
     * @return
     */
    public static Set<CubeTableSource> getAllCubeAbsentTables(long userId) {
        Set<CubeTableSource> absentTables = new HashSet<CubeTableSource>();
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            CubeTableSource source = businessTable.getTableSource();
            Set<CubeTableSource> tableLayers = toSet(source.createGenerateTablesList());
            for (CubeTableSource layer : tableLayers) {
                if (!BITableKeyUtils.isTableExisted(layer, cubeConfiguration)) {
                    absentTables.add(layer);
                }
            }
        }
        return absentTables;
    }


    /**
     * @param userId
     * @param tableSource
     * @return
     */
    public static boolean isTableExist(long userId, CubeTableSource tableSource) {
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        return BITableKeyUtils.isTableExisted(tableSource, cubeConfiguration);

    }

    public static Set<CubeTableSource> toSet(List<Set<CubeTableSource>> tableLayerTree) {
        Set<CubeTableSource> tableLayers = new HashSet<CubeTableSource>();
        for (Set<CubeTableSource> value : tableLayerTree) {
            tableLayers.addAll(value);
        }
        return tableLayers;
    }

    /**
     * 获得配置部分存在，但是在cube中缺少的关联
     *
     * @param userId 用户ID
     * @return 缺少的关联
     */
    public static Set<BITableSourceRelation> getCubeAbsentRelations(long userId) {
        Set<BITableRelation> currentRelations = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);

        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        Set<BITableSourceRelation> absentRelations = new HashSet<BITableSourceRelation>();
        BISystemConfigHelper converter = new BISystemConfigHelper();
        converter.prepare(userId);
        for (BITableRelation relation : currentRelations) {
            //部分businessTableRelation本身就有问题，在这里过滤掉
            if (!converter.isTableRelationValid(relation)) {
                logger.error("tableRelation invalid:" + relation.toString());
                continue;
            }

            BITableSourceRelation sourceRelation = null;
            try {
                sourceRelation = converter.convertRelation(relation);
            } catch (Exception e) {
                BILoggerFactory.getLogger(CubeUpdateUtils.class).error(e.getMessage(), e);
                continue;
            }
            if (sourceRelation != null && !BICubeRelationUtils.isRelationExisted(sourceRelation, cubeConfiguration)) {
                absentRelations.add(sourceRelation);
            }
        }
        return absentRelations;
    }

    /**
     * 获得配置部分存在，但是在cube中缺少的路径
     *
     * @param userId 用户ID
     * @return 缺少的路径
     */
    public static Set<BITableSourceRelationPath> getCubeAbsentPaths(long userId) {
        try {
            Set<BITableRelationPath> paths = BICubeConfigureCenter.getTableRelationManager().getAllTablePath(userId);
            ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
            Set<BITableSourceRelationPath> absentPaths = new HashSet<BITableSourceRelationPath>();
            BISystemConfigHelper converter = new BISystemConfigHelper();
            converter.prepare(userId);
            for (BITableRelationPath path : paths) {
                if (path.size() >= 2) {
                    BITableSourceRelationPath sourcePath = converter.convertPath(path);
                    if (sourcePath != null && !BICubePathUtils.isPathExisted(sourcePath, cubeConfiguration)) {
                        absentPaths.add(sourcePath);
                    }
                }
            }
            return absentPaths;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    /**
     * 优先级 all>part>never
     *
     * @param updateTypeSet
     * @return
     */
    public static int calcUpdateType(Collection<Integer> updateTypeSet) {
        if (updateTypeSet == null || updateTypeSet.isEmpty()) {
            return DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;
        } else if (updateTypeSet.contains(DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL)) {
            return DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;
        } else if (updateTypeSet.contains(DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART)) {
            return DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART;
        } else {
            return DBConstant.SINGLE_TABLE_UPDATE_TYPE.NEVER;
        }
    }

    /**
     * 优先级 all>part>never
     *
     * @param oldType
     * @param newType
     * @return
     */
    public static int calcUpdateType(Integer oldType, Integer newType) {
        return oldType <= newType ? oldType : newType;
    }

    public static boolean isPart(long userId) {
        ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
        ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf(Long.toString(userId)));
        Cube cube = new BICube(resourceRetrievalService, discovery);
        boolean isPart = (CubeUpdateUtils.getAllCubeAbsentTables(userId).size() > 0 || CubeUpdateUtils.getCubeAbsentRelations(userId).size() > 0 || CubeUpdateUtils.getCubeAbsentPaths(userId).size() > 0) && cube.isVersionAvailable();
        return isPart;
    }

    /**
     * @param userId
     * @return
     */
    public static boolean isUpdateMeta(long userId) {
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

    public static boolean isNeed2GenerateCube(long userId) {
        boolean isCHanged = !BICubeConfigureCenter.getTableRelationManager().isRelationNoChange(userId) || !BICubeConfigureCenter.getPackageManager().isTableNoChange(userId);
        return isCHanged||isPart(userId);
    }


}
