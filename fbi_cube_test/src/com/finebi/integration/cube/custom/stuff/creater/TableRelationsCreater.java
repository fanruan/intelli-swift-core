package com.finebi.integration.cube.custom.stuff.creater;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.integration.cube.custom.it.CustomDBTableSource;
import com.fr.bi.stable.data.BITableID;

/**
 * Created by Lucifer on 2017-6-1.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TableRelationsCreater {

    public static BITableRelation getTableRelationAB() {
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
        return relationAB;
    }

    public static BITableRelation getTableRelationBC() {
        BIBusinessTable tableB = new BIBusinessTable(new BITableID("bbbb"), "B");
        CustomDBTableSource tableSourceB = new CustomDBTableSource("BIdemo", "B");
        tableB.setSource(tableSourceB);
        BIBusinessTable tableC = new BIBusinessTable(new BITableID("cccc"), "C");
        CustomDBTableSource tableSourceC = new CustomDBTableSource("BIdemo", "C");
        tableC.setSource(tableSourceC);

        BusinessField primaryFieldB = new BIBusinessField("bbbb", "a");
        BusinessField foreignFieldC = new BIBusinessField("cccc", "a");
        BITableRelation relationBC = new BITableRelation(primaryFieldB, foreignFieldC);
        relationBC.setPrimaryTable(tableB);
        relationBC.setForeignTable(tableC);

        return relationBC;
    }

    public static BITableRelation getTableRelationCD() {
        BIBusinessTable tableC = new BIBusinessTable(new BITableID("cccc"), "C");
        CustomDBTableSource tableSourceC = new CustomDBTableSource("BIdemo", "C");
        tableC.setSource(tableSourceC);
        BIBusinessTable tableD = new BIBusinessTable(new BITableID("dddd"), "D");
        CustomDBTableSource tableSourceD = new CustomDBTableSource("BIdemo", "D");
        tableD.setSource(tableSourceD);

        BusinessField primaryFieldC = new BIBusinessField("cccc", "a");
        BusinessField foreignFieldD = new BIBusinessField("dddd", "a");
        BITableRelation relationCD = new BITableRelation(primaryFieldC, foreignFieldD);
        relationCD.setPrimaryTable(tableC);
        relationCD.setForeignTable(tableD);
        return relationCD;
    }
}
