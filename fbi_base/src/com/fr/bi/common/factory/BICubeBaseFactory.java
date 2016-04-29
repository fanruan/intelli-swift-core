package com.fr.bi.common.factory;

import com.fr.bi.stable.utils.code.BILogDelegate;

/**
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBaseFactory extends BIModuleFactory {
    @Override
    public void initialMateFactory() {
        try {
            registerFactory((IFactoryService) BIMateFactory.getInstance().getObject(IFactoryService.CUBE_BASE));
        } catch (Exception e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
    }

    @Override
    protected void setDefaultCurrentFactory() {
        useFactory(IFactoryService.CUBE_BASE);
    }

    @Override
    public String getFactoryTag() {
        return IModuleFactory.CUBE_BASE_MODULE;
    }
}
