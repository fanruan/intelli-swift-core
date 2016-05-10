package com.fr.bi.conf.base.pack;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.conf.base.pack.data.BIPackAndAuthority;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Connery on 2015/12/25.
 */

/**
 * TODO factory要修改，现在对象和持久化分开处理。那么不需要多个按照持久化的factory
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, implement = BIUserPackAndAuthConfigurationManager.class)
public class BIUserPackAndAuthConfigurationManager {

    protected BIUser user;
    protected BIPackAndAuthContainer biPackAndAuthContainer;



    public BIUserPackAndAuthConfigurationManager(long userId) {
        user = new BIUser(userId);
        biPackAndAuthContainer=new BIPackAndAuthContainer();
    }

    public BIPackAndAuthContainer getBiPackAndAuthContainer() {
        return biPackAndAuthContainer;
    }


    public BIUser getUser() {
        return user;
    }

    public void setUser(BIUser user) {
        this.user = user;
    }


    public Set<BIPackAndAuthority> getCurrentAuthority4Generating() {
        Set<BIPackAndAuthority> clone = new HashSet<BIPackAndAuthority>();
            for (BIPackAndAuthority pack : biPackAndAuthContainer.getAllPackages()) {
                clone.add(pack);
            }
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return false;
    }


}
