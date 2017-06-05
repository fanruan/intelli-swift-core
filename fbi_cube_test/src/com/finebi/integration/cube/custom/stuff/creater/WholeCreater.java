package com.finebi.integration.cube.custom.stuff.creater;

import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.exception.BITablePathConfusionException;
import com.fr.bi.util.BIConfUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Lucifer on 2017-6-1.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class WholeCreater {

    public static void abcentAll(Set<CubeTableSource> absentTables,
                                 Set<BITableSourceRelation> absentRelations,
                                 Set<BITableSourceRelationPath> absentPaths) throws BITablePathConfusionException {
        absentTables.add(TableSourceCreater.getTableSourceA());
        absentTables.add(TableSourceCreater.getTableSourceB());
        absentTables.add(TableSourceCreater.getTableSourceC());
        absentTables.add(TableSourceCreater.getTableSourceD());
        absentTables.add(TableSourceCreater.getTableSourceAB());
        absentTables.add(TableSourceCreater.getTableSourceCD());
        absentTables.add(TableSourceCreater.getTableSourceAB_CD());

        List<BITableRelation> tableRelationList = new ArrayList<BITableRelation>();
        tableRelationList.add(TableRelationsCreater.getTableRelationAB());
        tableRelationList.add(TableRelationsCreater.getTableRelationBC());
        tableRelationList.add(TableRelationsCreater.getTableRelationCD());
        absentRelations.addAll(BIConfUtils.convert2TableSourceRelation(tableRelationList));

        BITableSourceRelationPath biTableSourceRelationPathABC = new BITableSourceRelationPath();
        biTableSourceRelationPathABC.addRelationAtTail(BIConfUtils.convert2TableSourceRelation(TableRelationsCreater.getTableRelationAB()));
        biTableSourceRelationPathABC.addRelationAtTail(BIConfUtils.convert2TableSourceRelation(TableRelationsCreater.getTableRelationBC()));

        BITableSourceRelationPath biTableSourceRelationPathBCD = new BITableSourceRelationPath();
        biTableSourceRelationPathBCD.addRelationAtTail(BIConfUtils.convert2TableSourceRelation(TableRelationsCreater.getTableRelationBC()));
        biTableSourceRelationPathBCD.addRelationAtTail(BIConfUtils.convert2TableSourceRelation(TableRelationsCreater.getTableRelationCD()));

        BITableSourceRelationPath biTableSourceRelationPathABCD = new BITableSourceRelationPath();
        biTableSourceRelationPathABCD.addRelationAtTail(BIConfUtils.convert2TableSourceRelation(TableRelationsCreater.getTableRelationAB()));
        biTableSourceRelationPathABCD.addRelationAtTail(BIConfUtils.convert2TableSourceRelation(TableRelationsCreater.getTableRelationBC()));
        biTableSourceRelationPathABCD.addRelationAtTail(BIConfUtils.convert2TableSourceRelation(TableRelationsCreater.getTableRelationCD()));
        absentPaths.add(biTableSourceRelationPathABC);
        absentPaths.add(biTableSourceRelationPathBCD);
        absentPaths.add(biTableSourceRelationPathABCD);
    }
}
