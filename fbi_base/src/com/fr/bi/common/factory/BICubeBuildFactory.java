package com.fr.bi.common.factory;

import com.fr.bi.stable.utils.code.BILogDelegate;

/**
 * This class created on 2016/3/22.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBuildFactory extends BIModuleFactory {
    @Override
    public void initialMateFactory() {
        try {
            registerFactory((IFactoryService) BIMateFactory.getInstance().getObject(IFactoryService.CUBE_BASIC_BUILD));
        } catch (Exception e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
    }

    @Override
    protected void setDefaultCurrentFactory() {
        useFactory(IFactoryService.CUBE_BASIC_BUILD);
    }

    @Override
    public String getFactoryTag() {
        return IModuleFactory.CUBE_BUILD_MODULE;
    }
}
