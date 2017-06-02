package com.finebi.cube.impl.conf.helpers;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BISystemConfigHelper;
import com.finebi.cube.gen.oper.BIRelationIDUtils;
import com.finebi.cube.relation.BITableRelationPath;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class PathCalculateHelper {

    private static BILogger LOGGER = BILoggerFactory.getLogger(PathCalculateHelper.class);

    public static Set<BITableSourceRelationPath> calculateRelevantPath(BISystemConfigHelper configHelper, Set<BITableSourceRelation> relationInConstruction) {
        Set<String> relationIDs = new HashSet<String>();
        Set<BITableSourceRelationPath> pathsAboutRelation = new HashSet<BITableSourceRelationPath>();
        for (BITableSourceRelation relation : relationInConstruction) {
            relationIDs.add(BIRelationIDUtils.calculateRelationID(relation));
        }
        for (BITableRelationPath path : configHelper.getSystemTablePaths()) {
            /**
             * 等于1的path，实际就是relation了。这部分在relation地方处理了。
             */
            if (path.size() >= 2) {
                BITableSourceRelationPath sourceRelationPath = configHelper.convertPath(path);
                if (sourceRelationPath != null) {
                    for (BITableSourceRelation relation : sourceRelationPath.getAllRelations()) {
                        if (relationIDs.contains(BIRelationIDUtils.calculateRelationID(relation))) {
                            pathsAboutRelation.add(sourceRelationPath);
                            break;
                        }
                    }
                }
            }

        }
        return pathsAboutRelation;
    }

    public static Set<BITableSourceRelationPath> removePathAbsentRelation(Set<BITableSourceRelation> generatingRelations, Set<BITableSourceRelationPath> pathInConstruction,
                                                                          Set<BITableSourceRelation> absentRelations, Set<BITableSourceRelationPath> absentPaths) {
        Set<BITableSourceRelationPath> result = new HashSet<BITableSourceRelationPath>();
        for (BITableSourceRelationPath path : pathInConstruction) {
            boolean isNeed2Generate = true;
            for (BITableSourceRelation relation : path.getAllRelations()) {
                if (!isRelationExistOrGenerating(relation, generatingRelations, absentRelations)) {
                    isNeed2Generate = false;
                    break;
                }
            }
            if (isNeed2Generate && absentPaths.contains(path)) {
                result.add(path);
            }
        }
        return result;
    }

    /**
     * relation当前是否存在或者是否正要生成。
     *
     * @param relation
     * @param generatingRelations
     * @param absentRelations
     * @return
     */
    private static boolean isRelationExistOrGenerating(BITableSourceRelation relation, Set<BITableSourceRelation> generatingRelations,
                                                       Set<BITableSourceRelation> absentRelations) {
        if (generatingRelations.contains(relation) || !absentRelations.contains(relation)) {
            return true;
        }
        return false;
    }
}
