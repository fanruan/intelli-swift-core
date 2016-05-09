package com.fr.bi.conf.base.pack;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.common.inter.Release;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackageGetterService;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.base.pack.data.BIPackageName;
import com.fr.bi.conf.data.pack.exception.BIPackageAbsentException;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by Connery on 2015/12/22.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BIPackAndAuthConfigManager.class)
public class BIPackAndAuthConfigManager implements Release {


    /**
     * TODO group加入状态管理，减少通过比较Group来计算是否改变。
     */
    protected BIPackagesManagerService currentPackageManager;
    protected BIUser user;

    public BIPackAndAuthConfigManager(long userId) {
        user = BIFactoryHelper.getObject(BIUser.class, userId);
        currentPackageManager = BIFactoryHelper.getObject(BIPackagesManagerService.class, userId);
    }

    public BIUser getUser() {
        return user;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }



    public Set<BIBusinessPackage> getAllPackages() {
        synchronized (currentPackageManager) {
            return currentPackageManager.getAllPackages();
        }
    }


    public BIBusinessPackageGetterService getPackage(String packageId) throws BIPackageAbsentException {
        return getPackage(new BIPackageID(packageId));
    }

    public BIBusinessPackageGetterService getPackage(BIPackageID packageId) throws BIPackageAbsentException {
        return currentPackageManager.getPackage(packageId);
    }

    public void addPackage(BIBusinessPackage biBasicBusinessPackage) throws BIPackageDuplicateException {
        synchronized (currentPackageManager) {
            currentPackageManager.addPackage(biBasicBusinessPackage);
        }
    }





    public void removePackage(BIPackageID packageID) throws BIPackageAbsentException {
        synchronized (currentPackageManager) {
            currentPackageManager.removePackage(packageID);
        }
    }


    public Set<BIBusinessPackage> getPackageByName(BIPackageName packName) {
        HashSet<BIBusinessPackage> packages = new HashSet<BIBusinessPackage>();
        Iterator<BIBusinessPackage> it = currentPackageManager.getAllPackages().iterator();
        while (it.hasNext()) {
            BIBusinessPackage biBasicBusinessPackage = it.next();
            if (ComparatorUtils.equals(packName, biBasicBusinessPackage.getName())) {
                packages.add(biBasicBusinessPackage);
            }
        }
        return packages;
    }



    public JSONObject createPackageJSON() throws Exception {
        JSONObject jo = new JSONObject();
        Iterator<Map.Entry<Long, BIBusinessPackage>> iterator = sortPackages();
        while (iterator.hasNext()) {
            BIBusinessPackage pack = iterator.next().getValue();
            jo.put(pack.getID().getIdentity(), pack.createJSON());
        }
        return jo;
    }

    private Iterator<Map.Entry<Long, BIBusinessPackage>> sortPackages() {
        Map<Long, BIBusinessPackage> packageMap = new TreeMap<Long, BIBusinessPackage>(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return (o2 - o1) > 0 ? -1 : 1;
            }
        });
        for (BIBusinessPackage pack : currentPackageManager.getAllPackages()) {
            packageMap.put(pack.getPosition(), pack);
        }
        return packageMap.entrySet().iterator();
    }



    public void removeBusinessTableByID(BIPackageID packageID, BITableID tableID) throws BIPackageAbsentException, BITableAbsentException {
        currentPackageManager.removeTable(packageID, tableID);
    }

    public Set<Table> getAllTables() {
        Set<Table> result = new HashSet<Table>();
        Iterator<BIBusinessPackage> it = getAllPackages().iterator();
        while (it.hasNext()) {
            result.addAll(it.next().getBusinessTables());
        }
        return result;
    }

    @Override
    public void clear() {


    }
}
