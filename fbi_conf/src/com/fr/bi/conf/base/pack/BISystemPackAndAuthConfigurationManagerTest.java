package com.fr.bi.conf.base.pack;

import com.fr.bi.base.BIUser;
import com.fr.bi.conf.base.pack.data.BIBasicBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIPackageID;
import com.fr.bi.conf.base.pack.data.BIPackageName;
import com.fr.general.ComparatorUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wuk on 16/5/8.
 */
public class BISystemPackAndAuthConfigurationManagerTest {
    private BISystemPackAndAuthConfigurationManager manager;
    private BIUser user;

    protected void setUp() throws Exception {

    }

    @org.junit.Test
    public void getAllPackages() throws Exception {
        manager = new BISystemPackAndAuthConfigurationManager();
        user = new BIUser(999);
        manager.addPackage(user.getUserId(), new BIBasicBusinessPackage(new BIPackageID("新建业务包a")));
        manager.addPackage(user.getUserId(), new BIBasicBusinessPackage(new BIPackageID("新建业务包b")));
        Set<BIBusinessPackage> allPackages = manager.getAllPackages(user.getUserId());
//        manager.persistData(user.getUserId());
        HashSet<BIBusinessPackage> packages = new HashSet<BIBusinessPackage>();
        Iterator<BIBusinessPackage> it = allPackages.iterator();
        while (it.hasNext()) {
            BIBusinessPackage biBasicBusinessPackage = it.next();
            if (ComparatorUtils.equals(new BIPackageName("BI_EMPTY_NAME"), biBasicBusinessPackage.getName())) {
                packages.add(biBasicBusinessPackage);
            }
        }




    }

    @org.junit.Test
    public void getPackage() throws Exception {

    }

    @org.junit.Test
    public void addPackage() throws Exception {

    }

    @org.junit.Test
    public void removePackage() throws Exception {

    }

}
