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
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.data.source.SQLTableSource;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.io.File;
import java.util.*;

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

    private static Set<CubeTableSource> toSet(List<Set<CubeTableSource>> tableLayerTree) {
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

    public static Map recordTableAndRelationInfo(long userId) {
        Set<CubeTableSource> sources = new HashSet<CubeTableSource>();
        Set<BusinessTable> allTables = BICubeConfigureCenter.getPackageManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            sources.add(table.getTableSource());
        }
        Map<String, String> sourceInfo = new HashMap<String, String>();
        for (CubeTableSource source : sources) {
            if (source instanceof SQLTableSource) {
                String queryName = ((SQLTableSource) source).getSqlConnection() + "@" + ((SQLTableSource) source).getQuery() + ((SQLTableSource) source).getSqlConnection();
                sourceInfo.put(queryName, source.getSourceID());
            }
            if (source instanceof DBTableSource) {
                sourceInfo.put(source.toString(), source.getSourceID());
            }
            if (source.getType() == BIBaseConstant.TABLETYPE.ETL) {
                StringBuffer EtlInfo = new StringBuffer();
                EtlInfo.append("ETL,parents as listed：");
                for (CubeTableSource tableSource : ((ETLTableSource) source).getParents()) {
                    EtlInfo.append(tableSource.getSourceID() + tableSource.getTableName());
                }
                sourceInfo.put(EtlInfo.toString(), source.getSourceID());
            }
        }
        Map<String, String> relationMap = new HashMap<String, String>();
        Set<BITableRelation> tableRelations = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
        for (BITableRelation relation : tableRelations) {
            relationMap.put(relation.getPrimaryTable().getTableSource().toString() + "." + relation.getPrimaryField().getFieldName() + ">>" + relation.getForeignTable().getTableSource().toString() + "." + relation.getForeignField().getFieldName(), relation.getPrimaryTable().getTableSource().getSourceID() + "||" + relation.getForeignTable().getTableSource().getSourceID());
        }
        ICubeConfiguration advancedConf = BICubeConfiguration.getConf(Long.toString(userId));
        String cubeTablePath = new File(advancedConf.getRootURI().getPath()).getParent() + File.separatorChar + "tableInfo";
        String cubeRelationPath = new File(advancedConf.getRootURI().getPath()).getParent() + File.separatorChar + "relationInfo";
        BIFileUtils.writeFile(cubeTablePath, sourceInfo.toString());
        BIFileUtils.writeFile(cubeRelationPath, relationMap.toString());
        Map<String, String> info = new HashMap();
        info.put("tableInfo", sourceInfo.toString());
        info.put("relationInfo", relationMap.toString());
        return info;
    }
}
