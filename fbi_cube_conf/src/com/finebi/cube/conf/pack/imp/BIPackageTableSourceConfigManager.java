package com.finebi.cube.conf.pack.imp;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BIPackageTableSourceConfigProvider;
import com.finebi.cube.conf.BISystemPackageConfigurationProvider;
import com.finebi.cube.conf.pack.data.BIBusinessPackage;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wuk on 16/6/8.
 */
public class BIPackageTableSourceConfigManager implements BIPackageTableSourceConfigProvider {
private BISystemPackageConfigurationProvider packageManager;
    @Override
    public Set<BIBusinessTable> getTableSources4Genrate(long userId) {
        packageManager = BICubeConfigureCenter.getPackageManager();
        Set<BIBusinessPackage> packages4CubeGenerate = packageManager.getPackages4CubeGenerate(userId);
        Set<IBusinessPackageGetterService> iBusinessPackageGetterServiceSet=new HashSet<IBusinessPackageGetterService>();
        for (BIBusinessPackage biBusinessPackage : packages4CubeGenerate) {
            try {
                IBusinessPackageGetterService iBusinessPackageGetterService = BICubeConfigureCenter.getPackageManager().getPackage(userId, biBusinessPackage.getID());
                iBusinessPackageGetterServiceSet.add(iBusinessPackageGetterService);
            } catch (BIPackageAbsentException e) {
                BILogger.getLogger().error(e.getMessage());
            }
        }
        Set<BIBusinessTable> sources = getTableSources(iBusinessPackageGetterServiceSet, userId);
        return sources;
    }

    public Set<BIBusinessTable> getTableSources(Set<IBusinessPackageGetterService> packs, long userId) {
        Set<BIBusinessTable> sources = new HashSet<BIBusinessTable>();
        for (IBusinessPackageGetterService pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                sources.add(table);
            }
        }
        return sources;
    }
}
