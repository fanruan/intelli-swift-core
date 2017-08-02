package com.finebi.cube.impl.conf.helpers;

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
     * @param relations
     * @return
     */
    public static Set<BITableSourceRelation> calculateRelevantRelation(Set<String> allSourceIds, Set<BITableSourceRelation> relations) {
        Set<BITableSourceRelation> relationsAboutTable = new HashSet<BITableSourceRelation>();
        for (BITableSourceRelation relation : relations) {
            if (allSourceIds.contains(relation.getPrimaryTable().getSourceID())
                    || allSourceIds.contains(relation.getForeignTable().getSourceID())) {
                relationsAboutTable.add(relation);
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
                result.add(relation);
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
        Set<String> generatingTableIds = new HashSet<String>();
        for (CubeTableSource generatingTable : generatingTables) {
            generatingTableIds.add(generatingTable.getSourceID());
        }
        Set<String> absentTableIds = new HashSet<String>();
        for (CubeTableSource absentTable : absentTables) {
            absentTableIds.add(absentTable.getSourceID());
        }
        if (generatingTableIds.contains(tableSource.getSourceID()) || !absentTableIds.contains(tableSource.getSourceID())) {
            return true;
        }
        return false;
    }
}
