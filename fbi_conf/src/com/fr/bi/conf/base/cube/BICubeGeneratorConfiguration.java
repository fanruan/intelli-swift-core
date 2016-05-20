package com.fr.bi.conf.base.cube;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.ICubeGeneratorConfigure;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.relation.BITableRelationPath;

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

        Iterator<BIBusinessPackage> allPackages = BIConfigureManagerCenter.getPackageManager().getAllPackages(userId).iterator();
        while (allPackages.hasNext()) {
            BIBusinessPackage businessPackage = allPackages.next();
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
