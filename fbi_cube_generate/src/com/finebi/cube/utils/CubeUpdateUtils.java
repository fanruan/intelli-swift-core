package com.finebi.cube.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BISystemConfigHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kary on 16/7/14.
 */
public class CubeUpdateUtils {

    /*是否需要更新cube*/
    public static boolean cubeStatusCheck(long userId) {
        return false;
    }

    /**
     * 获得配置部分存在，但是在cube中缺少的表
     *
     * @param userId 用户ID
     * @return 缺失的表
     */
    public static Set<CubeTableSource> getCubeAbsentTables(long userId) {
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
                BILoggerFactory.getLogger().error("tableRelation invalid:" + relation.toString());
                continue;
            }
            BITableSourceRelation sourceRelation = converter.convertRelation(relation);
            if (!BICubeRelationUtils.isRelationExisted(sourceRelation, cubeConfiguration)) {
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
                    if (!BICubePathUtils.isPathExisted(sourcePath, cubeConfiguration)) {
                        absentPaths.add(sourcePath);
                    }
                }
            }
            return absentPaths;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
