package com.finebi.integration.cube.custom.it;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.relation.BISystemTableRelationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.exception.BITablePathConfusionException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-3-17.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomRelationProvider extends BISystemTableRelationManager {

    Set<BITableRelation> biTableRelationSet = new HashSet<BITableRelation>();
    Set<BITableRelationPath> biTableRelationPathSet = new HashSet<BITableRelationPath>();

    public CustomRelationProvider() throws BITablePathConfusionException {

        BIBusinessTable tableA = new BIBusinessTable(new BITableID("aaaa"), "A");
        CustomDBTableSource tableSourceA = new CustomDBTableSource("BIdemo", "A");
        tableA.setSource(tableSourceA);
        BIBusinessTable tableB = new BIBusinessTable(new BITableID("bbbb"), "B");
        CustomDBTableSource tableSourceB = new CustomDBTableSource("BIdemo", "B");
        tableB.setSource(tableSourceB);
        BIBusinessTable tableC = new BIBusinessTable(new BITableID("cccc"), "C");
        CustomDBTableSource tableSourceC = new CustomDBTableSource("BIdemo", "C");
        tableC.setSource(tableSourceC);

        BusinessField primaryFieldA = new BIBusinessField("aaaa", "a");
        BusinessField foreignFieldB = new BIBusinessField("bbbb", "a");
        BITableRelation relationAB = new BITableRelation(primaryFieldA, foreignFieldB);
        relationAB.setPrimaryTable(tableA);
        relationAB.setForeignTable(tableB);
        BusinessField primaryFieldB = new BIBusinessField("bbbb", "a");
        BusinessField foreignFieldC = new BIBusinessField("cccc", "a");
        BITableRelation relationBC = new BITableRelation(primaryFieldB, foreignFieldC);
        relationBC.setPrimaryTable(tableB);
        relationBC.setForeignTable(tableC);
        biTableRelationSet.add(relationAB);
        biTableRelationSet.add(relationBC);

        BITableRelation[] biTableRelations = new BITableRelation[2];
        biTableRelations[0] = relationAB;
        biTableRelations[1] = relationBC;
        BITableRelationPath biTableRelationPath = new BITableRelationPath(biTableRelations);
        biTableRelationPathSet.add(biTableRelationPath);
    }

    @Override
    public Set<BITableRelation> getAllTableRelation(long userId) {
        return biTableRelationSet;
    }

    @Override
    public Set<BITableRelationPath> getAllTablePath(long userId) {
        return biTableRelationPathSet;
    }
}
