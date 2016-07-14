package com.fr.bi.cal.generate.timerTask.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.conf.BICubeConfiguration;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ITableKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BIRelationAbsentException;
import com.fr.bi.stable.exception.BITableAbsentException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuk on 16/7/13.
 */
public class CubeUpdateUtils {
    private static boolean forceCheck=false;
    /*是否需要更新cube*/
    public static boolean cubeStatusCheck(long userId) {
        return isPart(userId)&&forceCheck;
    }

    private static boolean isPart(long userId) {
        return getNewTables(userId).size() > 0 || getNewRelations(userId).size() > 0;
    }

    /* 获取所有新增的table*/
    public static Set<BIBusinessTable> getNewTables(long userId) {
        Set<BIBusinessTable> newTables = new HashSet<BIBusinessTable>();
        for (BusinessTable businessTable : BICubeConfigureCenter.getPackageManager().getAllTables(userId)) {
            if (!tableExisted(businessTable.getTableSource(), userId)) {
                newTables.add((BIBusinessTable) businessTable);
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


    private static boolean tableExisted(CubeTableSource source, long userId) {
        ICubeConfiguration cubeConfiguration = BICubeConfiguration.getConf(Long.toString(userId));
        BICube iCube = new BICube(new BICubeResourceRetrieval(cubeConfiguration), BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        ITableKey iTableKey = new BITableKey(source);
        return iCube.exist(iTableKey);
    }

    public static void setForceCheck(boolean forceCheck) {
        CubeUpdateUtils.forceCheck = forceCheck;
    }
}
