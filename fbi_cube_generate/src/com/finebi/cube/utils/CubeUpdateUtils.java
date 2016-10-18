package com.finebi.cube.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.base.BICore;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.conf.data.source.ETLTableSource;
import com.fr.bi.conf.data.source.SQLTableSource;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.file.BIFileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kary on 16/7/14.
 */
public class CubeUpdateUtils {

    /*是否需要更新cube*/
    public static boolean cubeStatusCheck(long userId) {
        return false;
    }

    public static boolean isNeedUpdate(long userId) {
        return getNewTables(userId).size() > 0 || getNewRelations(userId).size() > 0;
    }

    /* 获取所有新增的table*/
    public static Set<BIBusinessTable> getNewTables(long userId) {
        Set<BIBusinessTable> newTables = new HashSet<BIBusinessTable>();
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            CubeTableSource source = businessTable.getTableSource();
            Map<BICore, CubeTableSource> sourceMap = source.createSourceMap();
            for (BICore core : sourceMap.keySet()) {
                    if (!BITableKeyUtils.isTableExisted(sourceMap.get(core), cubeConfiguration)) {
                        newTables.add((BIBusinessTable) businessTable);
                        break;
                    }
                }
        }
        return newTables;
    }

    /* 获取所有新增的relation*/
    public static Set<BITableRelation> getNewRelations(long userId) {
        Set<BITableRelation> currentRelations = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
        Set<BITableRelation> newRelationSet = new HashSet<BITableRelation>();
        for (BITableRelation relation : currentRelations) {
            try {
                if (!BICubeConfigureCenter.getTableRelationManager().isRelationGenerated(userId, relation)) {
                    newRelationSet.add(relation);
                }
            } catch (BITableAbsentException e) {
                e.printStackTrace();
            } catch (BIRelationAbsentException e) {
                e.printStackTrace();
            }
        }
        return newRelationSet;
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
