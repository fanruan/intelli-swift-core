package com.fr.bi.common.factory;


import com.fr.bi.stable.utils.code.BILogDelegate;

/**
 * Created by Connery on 2015/12/7.
 */
public class BIConfFactory extends BIModuleFactory {
    public BIConfFactory() {
        super();
    }

    @Override
    public void initialMateFactory() {
        try {
            registerFactory((BIDBConfFactory) BIMateFactory.getInstance().getObject(IFactoryService.CONF_DB));
            registerFactory((BIXMLConfFactory) BIMateFactory.getInstance().getObject(IFactoryService.CONF_XML));

        } catch (Exception e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
    }

    @Override
    protected void setDefaultCurrentFactory() {
        useFactory(IFactoryService.CONF_XML);
    }

    @Override
    public String getFactoryTag() {
        return IModuleFactory.CONF_MODULE;
    }
}