package com.finebi.cube.impl.conf.helpers;

import com.finebi.cube.conf.BISystemConfigHelper;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-5-24.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class RelationCalculateHelper {

    /**
     * 计算和tableInConstruction中所有tableSource有关的relation
     *
     * @param allSourceIds
     * @param configHelper
     * @return
     */
    public static Set<BITableSourceRelation> calculateRelevantRelation(Set<String> allSourceIds, BISystemConfigHelper configHelper) {
        Set<BITableSourceRelation> relationsAboutTable = new HashSet<BITableSourceRelation>();
        for (BITableRelation relation : configHelper.getSystemTableRelations()) {
            BITableSourceRelation relevantSourceRelation = configHelper.convertRelation(relation);
            if (relevantSourceRelation == null) {
                continue;
            }
            if (allSourceIds.contains(relation.getPrimaryTable().getTableSource().getSourceID())
                    || allSourceIds.contains(relation.getForeignTable().getTableSource().getSourceID())) {
                relationsAboutTable.add(configHelper.convertRelation(relation));
            }
        }
        return relationsAboutTable;
    }

    /**
     * 如果relationInConstruction中的relation中一端的表并且不在specificTables中，则去掉。否则就更新。
     *
     * @param generatingTables
     * @param relationInConstruction
     * @param absentTables
     * @return
     */
    public static Set<BITableSourceRelation> removeRelationAbsentTable(Set<CubeTableSource> generatingTables, Set<BITableSourceRelation> relationInConstruction,
                                                                       Set<CubeTableSource> absentTables, Set<BITableSourceRelation> absentRelations) {
        Set<BITableSourceRelation> result = new HashSet<BITableSourceRelation>();
        for (BITableSourceRelation relation : relationInConstruction) {
            CubeTableSource primaryTable = relation.getPrimaryTable();
            CubeTableSource foreignTable = relation.getForeignTable();
            if (isTableExistOrGenerating(primaryTable, generatingTables, absentTables)
                    && isTableExistOrGenerating(foreignTable, generatingTables, absentTables)) {
                if (absentRelations.contains(relation)) {
                    result.add(relation);
                }
            }
        }
        return result;
    }


    /**
     * tableSource当前是否存在或者是否正要生成。
     *
     * @param tableSource
     * @param generatingTables
     * @param absentTables
     * @return
     */
    private static boolean isTableExistOrGenerating(CubeTableSource tableSource, Set<CubeTableSource> generatingTables,
                                                    Set<CubeTableSource> absentTables) {
        if (generatingTables.contains(tableSource) || !absentTables.contains(tableSource)) {
            return true;
        }
        return false;
    }
}
