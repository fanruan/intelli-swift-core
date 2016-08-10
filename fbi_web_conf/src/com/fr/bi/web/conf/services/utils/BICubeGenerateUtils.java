package com.fr.bi.web.conf.services.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ITableKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 16/6/8.
 * 为cube提供部分计算方法
 */
public class BICubeGenerateUtils {

    /* 获取所有新增的table*/
    public static Set<BIBusinessTable> getTables4CubeGenerate(long userId) {
        Set<BIBusinessTable> newTables = new HashSet<BIBusinessTable>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
                if(!tableExisted(businessTable.getTableSource(),userId)) {
                newTables.add((BIBusinessTable) businessTable);
            }
        }
        return newTables;
    }

    /* 获取所有新增的relation*/
    public static Set<BITableRelation> getRelations4CubeGenerate(long userId) {
        Set<BITableRelation> currentRelations = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
        Set<BITableRelation> newRelationSet = new HashSet<BITableRelation>();
        for (BITableRelation relation : currentRelations) {
            try {
                if (!BICubeConfigureCenter.getTableRelationManager().isRelationGenerated(userId,relation)) {
                    if (isRelationValid(relation)) {
                        newRelationSet.add(relation);
                    }
                }
            } catch (BITableAbsentException e) {
                BILogger.getLogger().error(e.getMessage());
            } catch (BIRelationAbsentException e) {
                BILogger.getLogger().error(e.getMessage());
            }
        }
        return newRelationSet;
    }
    

    public static boolean tableExisted(CubeTableSource source, long userId) {
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        ICubeResourceRetrievalService retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        BICube iCube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        ITableKey iTableKey = new BITableKey(source);
        boolean isExsited = iCube.getCubeTableWriter(iTableKey).isVersionAvailable() && iCube.getCubeTableWriter(iTableKey).isCubeLastTimeAvailable();
//        return iCube.exist(iTableKey);
        return isExsited;
    }

    private static boolean isRelationValid(BITableRelation relation) {
        boolean checkNull= null!=relation.getPrimaryTable()&&null!=relation.getForeignTable()&&null!=relation.getPrimaryField()&&null!=relation.getForeignField();
        boolean isTableValid=relation.getForeignField().getTableBelongTo().getID().getIdentity().equals(relation.getForeignTable().getID().getIdentity())&&relation.getPrimaryField().getTableBelongTo().getID().getIdentity().equals(relation.getPrimaryTable().getID().getIdentity());
        return checkNull&&isTableValid;
    }
}
