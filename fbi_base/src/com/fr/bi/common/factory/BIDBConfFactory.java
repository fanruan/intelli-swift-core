package com.fr.bi.common.factory;

import com.fr.bi.common.factory.annotation.BISingletonObject;

/**
 * Created by Connery on 2015/12/7.
 */
@BISingletonObject
class BIDBConfFactory extends BIBeanFactory {

    @Override
    public String getFactoryTag() {
        return IFactoryService.CONF_DB;
    }
}