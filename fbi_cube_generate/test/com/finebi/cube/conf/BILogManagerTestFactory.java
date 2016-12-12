package com.finebi.cube.conf;

import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.conf.log.BILogManager;
import com.fr.bi.conf.provider.BILogManagerProvider;
import com.fr.bi.conf.report.widget.RelationColumnKey;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BIStringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class created on 16-12-12.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class BILogManagerTestFactory {
    static BILogManagerProvider biLogManager = new BILogManager();

    public static BILogManagerProvider generateBILogs(long userId, Set<CubeTableSource> correctSources, Set<CubeTableSource> errSources, Set<BITableSourceRelation> correctPaths, Set<BITableSourceRelation> errorPaths) {
        biLogManager.logStart(System.currentTimeMillis());
        recordTableInfo(userId, correctSources, errSources);
        recordPathInfo(userId, correctPaths, errorPaths);
        biLogManager.logEnd(System.currentTimeMillis());
        return biLogManager;
    }

    private static void recordPathInfo(long userId, Set<BITableSourceRelation> correctPaths, Set<BITableSourceRelation> errorPaths) {
        biLogManager.infoRelation(getRelationColumnKeyInfo(correctPaths), 100, userId);
        biLogManager.errorRelation(getRelationColumnKeyInfo(errorPaths), errPathsLog(errorPaths), userId);
        Set<BITableSourceRelationPath> allPaths=new HashSet<BITableSourceRelationPath>();
        for (BITableSourceRelation relation : correctPaths) {
            BITableSourceRelationPath path=new BITableSourceRelationPath(relation);
           allPaths.add(path);
        }
        for (BITableSourceRelation relation : errorPaths) {
            BITableSourceRelationPath path=new BITableSourceRelationPath(relation);
            allPaths.add(path);
        }
        biLogManager.relationPathSet(allPaths, userId);
    }


    private static void recordTableInfo(long userId, Set<CubeTableSource> correctSources, Set<CubeTableSource> errSources) {
        for (CubeTableSource cubeTableSource : correctSources) {
            biLogManager.infoTable(cubeTableSource.getPersistentTable(), 1000, userId);
            recordColumnInfo(userId, cubeTableSource);
        }
        for (CubeTableSource cubeTableSource : errSources) {
            biLogManager.errorTable(cubeTableSource.getPersistentTable(), errorTableLog(cubeTableSource), userId);
            recordColumnInfo(userId, cubeTableSource);
        }
        Set<CubeTableSource> allSources=new HashSet<CubeTableSource>();
        allSources.addAll(correctSources);
        allSources.addAll(errSources);
        biLogManager.cubeTableSourceSet(allSources, userId);
    }

    private static String errorTableLog(CubeTableSource cubeTableSource) {
        return BIStringUtils.append(cubeTableSource.getTableName(), "\n", "error_text");
    }

    private static String errPathsLog(Set<BITableSourceRelation> errorPaths) {
        return BIStringUtils.append(errorPaths.toString(), "\n", "error_text");
    }

    private static void recordColumnInfo(long userId, CubeTableSource cubeTableSource) {
        ICubeFieldSource[] fieldsArray = cubeTableSource.getFieldsArray(null);
        for (ICubeFieldSource iCubeFieldSource : fieldsArray) {
            biLogManager.infoColumn(iCubeFieldSource.getTableBelongTo().getPersistentTable(), iCubeFieldSource.getFieldName(), 100, userId);
        }
    }


    private static RelationColumnKey getRelationColumnKeyInfo(Set<BITableSourceRelation> relations) {
        if (relations.size() > 0) {
            ICubeFieldSource field = null;
            List<BITableSourceRelation> list = new ArrayList<BITableSourceRelation>();
            for (BITableSourceRelation relation : relations) {
                field = relation.getPrimaryField();
                list.add(relation);
            }
            return new RelationColumnKey(field, list);
        }else
        return null;
    }
}
