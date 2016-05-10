package com.fr.bi.conf.base.pack;

import com.fr.bi.base.BIUser;
import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;


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


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return false;
    }


}
