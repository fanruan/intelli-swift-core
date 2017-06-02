package com.finebi.integration.cube.custom.stuff;

import com.finebi.cube.conf.pack.imp.BISystemPackageConfigurationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.integration.cube.custom.stuff.creater.TableSourceCreater;
import com.fr.bi.stable.data.BITableID;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lucifer on 2017-6-1.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class StuffPackageProvider extends BISystemPackageConfigurationManager {

    private Set<BusinessTable> businessTableSet = new HashSet<BusinessTable>();

    public StuffPackageProvider() {
        BIBusinessTable tableA = new BIBusinessTable(new BITableID("aaaa"), "A");
        tableA.setSource(TableSourceCreater.getTableSourceA());

        BIBusinessTable tableB = new BIBusinessTable(new BITableID("bbbb"), "B");
        tableB.setSource(TableSourceCreater.getTableSourceB());

        BIBusinessTable tableC = new BIBusinessTable(new BITableID("cccc"), "C");
        tableC.setSource(TableSourceCreater.getTableSourceC());

        BIBusinessTable tableD = new BIBusinessTable(new BITableID("dddd"), "D");
        tableD.setSource(TableSourceCreater.getTableSourceD());

        BIBusinessTable tableAB = new BIBusinessTable(new BITableID("aabb"), "AB");
        tableAB.setSource(TableSourceCreater.getTableSourceAB());

//        BIBusinessTable tableBC = new BIBusinessTable(new BITableID("bbcc"), "BC");
//        tableBC.setSource(TableSourceCreater.getTableSourceBC());

        BIBusinessTable tableCD = new BIBusinessTable(new BITableID("ccdd"), "CD");
        tableCD.setSource(TableSourceCreater.getTableSourceCD());

        BIBusinessTable tableABCD = new BIBusinessTable(new BITableID("abcd"), "ABCD");
        tableCD.setSource(TableSourceCreater.getTableSourceAB_CD());

        businessTableSet.add(tableA);
        businessTableSet.add(tableB);
        businessTableSet.add(tableC);
        businessTableSet.add(tableD);
        businessTableSet.add(tableAB);
//        businessTableSet.add(tableBC);
        businessTableSet.add(tableCD);
        businessTableSet.add(tableABCD);
    }

    @Override
    public Set<BusinessTable> getAllTables(long userId) {
        return businessTableSet;
    }
}
