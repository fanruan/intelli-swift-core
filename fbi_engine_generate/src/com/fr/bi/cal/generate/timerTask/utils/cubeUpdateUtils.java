package com.fr.bi.cal.generate.timerTask.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
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

import java.util.Set;

/**
 * Created by wuk on 16/7/13.
 */
public  class CubeUpdateUtils {
    /*是否需要更新cube*/
    public static boolean isPart(long userId) {
        return newTableSourceExisted(userId) || newRelationExisted(userId);
    }

    /*是否有需要新增的表*/
    private static boolean newTableSourceExisted(long userId) {
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            if (!tableExisted(businessTable.getTableSource(), userId)) {
                return true;
            }
        }
        return false;
    }

    /* 是否有新增的relation*/
    private static boolean newRelationExisted(long userId) {
        Set<BITableRelation> allTableRelation = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
        Set<BITableRelation> oldRelation = BICubeConfigureCenter.getTableRelationManager().getAnalysisAllTableRelation(userId);
        for (BITableRelation relation : allTableRelation) {
            if (!oldRelation.contains(relation)) {
                return true;
            }
        }
        return false;
    }

    public static boolean tableExisted(CubeTableSource source, long userId) {
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        ICubeResourceRetrievalService retrievalService = new BICubeResourceRetrieval(cubeConfiguration);
        ITableKey iTableKey = new BITableKey(source);
        BICube iCube = new BICube(retrievalService, BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        return iCube.exist(iTableKey);
    }
}
