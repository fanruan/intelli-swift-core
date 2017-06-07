package com.finebi.integration.cube.custom.stuff.creater;

import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.stable.exception.BITablePathConfusionException;

/**
 * Created by Lucifer on 2017-6-1.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TablePathCreater {
    public static BITableRelationPath getPathABC() throws BITablePathConfusionException {
        BITableRelation[] biTableRelations = new BITableRelation[2];
        biTableRelations[0] = TableRelationsCreater.getTableRelationAB();
        biTableRelations[1] = TableRelationsCreater.getTableRelationBC();
        BITableRelationPath pathABC = new BITableRelationPath(biTableRelations);
        return pathABC;
    }


    public static BITableRelationPath getPathBCD() throws BITablePathConfusionException {
        BITableRelation[] biTableRelations = new BITableRelation[2];
        biTableRelations[0] = TableRelationsCreater.getTableRelationBC();
        biTableRelations[1] = TableRelationsCreater.getTableRelationCD();
        BITableRelationPath pathBCD = new BITableRelationPath(biTableRelations);
        return pathBCD;
    }

    public static BITableRelationPath getPathABCD() throws BITablePathConfusionException {
        BITableRelation[] biTableRelations = new BITableRelation[3];
        biTableRelations[0] = TableRelationsCreater.getTableRelationAB();
        biTableRelations[1] = TableRelationsCreater.getTableRelationBC();
        biTableRelations[2] = TableRelationsCreater.getTableRelationCD();
        BITableRelationPath pathABCD = new BITableRelationPath(biTableRelations);
        return pathABCD;
    }

}
