package com.fr.bi.conf.base.pack;

import com.fr.bi.common.container.BISetContainer;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.base.pack.data.BIPackAndAuthority;
import com.fr.bi.conf.data.pack.exception.BIPackageDuplicateException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by wuk on 16/5/9.
 * 业务包权限的容器
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML)
public class BIPackAndAuthContainer extends BISetContainer<BIPackAndAuthority> {


    @Override
    protected Object clone() throws CloneNotSupportedException {
        BIPackAndAuthContainer biPackAndAuthContainer = (BIPackAndAuthContainer) super.clone();
        biPackAndAuthContainer.container = initCollection();
        try {
            Iterator<BIPackAndAuthority> it = this.container.iterator();
            while (it.hasNext()) {
                BIPackAndAuthority t=new BIPackAndAuthority();
                t = it.next();
                biPackAndAuthContainer.container.add(t);
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return biPackAndAuthContainer;
    }

    @Override
    protected Set<BIPackAndAuthority> getContainer() {
        return super.getContainer();
    }

    public Set<BIPackAndAuthority> getAllPackages() {
        return this.getContainer();
    }

    public void addPackage(BIPackAndAuthority biPackAndAuthority) throws BIPackageDuplicateException {
        if (!this.contain(biPackAndAuthority)) {
            this.add(biPackAndAuthority);
        } else {
            throw new BIPackageDuplicateException();
        }
    }

    public Boolean containPackage(BIPackAndAuthority biPackAndAuthority) {
        return super.contain(biPackAndAuthority);
    }

}
