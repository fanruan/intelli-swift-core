package com.finebi.integration.cube.custom.it;

import com.finebi.cube.conf.pack.imp.BISystemPackageConfigurationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.stable.data.BITableID;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-3-17.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class CustomPackageProvider extends BISystemPackageConfigurationManager {

    private Set<BusinessTable> businessTableSet = new HashSet<BusinessTable>();

    public CustomPackageProvider() {
        BIBusinessTable tableA = new BIBusinessTable(new BITableID("aaaa"), "A");
        tableA.setSource(CustomTableCreater.getTableSourceA());

        BIBusinessTable tableB = new BIBusinessTable(new BITableID("bbbb"), "B");
        tableB.setSource(CustomTableCreater.getTableSourceB());

        BIBusinessTable tableC = new BIBusinessTable(new BITableID("cccc"), "C");
        tableC.setSource(CustomTableCreater.getTableSourceC());

        BIBusinessTable tableD = new BIBusinessTable(new BITableID("dddd"), "D");
        tableD.setSource(CustomTableCreater.getTableSourceD());

        BIBusinessTable tableAB = new BIBusinessTable(new BITableID("aabb"), "AB");
        tableAB.setSource(CustomTableCreater.getTableSourceAB());

        BIBusinessTable tableBC = new BIBusinessTable(new BITableID("bbcc"), "BC");
        tableBC.setSource(CustomTableCreater.getTableSourceBC());

        BIBusinessTable tableCD = new BIBusinessTable(new BITableID("ccdd"), "CD");
        tableCD.setSource(CustomTableCreater.getTableSourceCD());

        businessTableSet.add(tableA);
        businessTableSet.add(tableB);
        businessTableSet.add(tableC);
        businessTableSet.add(tableD);
        businessTableSet.add(tableAB);
        businessTableSet.add(tableBC);
        businessTableSet.add(tableCD);
    }

    @Override
    public Set<BusinessTable> getAllTables(long userId) {
        return businessTableSet;
    }
}
