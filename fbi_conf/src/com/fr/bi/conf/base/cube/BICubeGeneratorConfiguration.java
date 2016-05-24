package com.fr.bi.conf.base.cube;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.ICubeGeneratorConfigure;
import com.fr.bi.stable.data.source.ICubeTableSource;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class created on 2016/3/8.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeGeneratorConfiguration implements ICubeGeneratorConfigure {
    @Override
    public Set<ICubeTableSource> getAllTableData(long userId) {
        Set<ICubeTableSource> allTable = new HashSet<ICubeTableSource>();

        Iterator<IBusinessPackageGetterService> allPackages = BICubeConfigureCenter.getPackageManager().getAllPackages(userId).iterator();
        while (allPackages.hasNext()) {
            IBusinessPackageGetterService businessPackage = allPackages.next();
            Iterator<BIBusinessTable> itTable = businessPackage.getBusinessTables().iterator();
            while (itTable.hasNext()) {
                BIBusinessTable biBusinessTable = itTable.next();
                ICubeTableSource tableSource = BIConfigureManagerCenter.getDataSourceManager().getTableSourceByID(biBusinessTable.getID(), new BIUser(userId));
                allTable.add(tableSource);
            }
        }
        return allTable;
    }

    @Override
    public Set<BITableRelationPath> getAllPathsData(long userId) {
        return null;
    }
}
