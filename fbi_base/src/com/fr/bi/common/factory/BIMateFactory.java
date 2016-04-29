package com.fr.bi.common.factory;

import com.fr.bi.stable.utils.code.BILogDelegate;

/**
 * 保存着Factory的Factory
 * Created by Connery on 2015/12/8.
 */
public class BIMateFactory extends BIBeanFactory {
    private static BIMateFactory instance;

    private BIMateFactory() {
        try {
            registerObject(IFactoryService.CONF_DB, new BIDBConfFactory());
            registerObject(IFactoryService.CONF_XML, new BIXMLConfFactory());
            registerObject(IFactoryService.CUBE_BASE, new BIFileCubeBaseFactory());
            registerObject(IFactoryService.CUBE_BASIC_BUILD, new BIBasicCubeBuildFactory());


        } catch (BIClassNonsupportException e) {
            BILogDelegate.errorDelegate(e.getMessage(), e);
        }
    }

    public static BIMateFactory getInstance() {
        if (instance != null) {
            return instance;
        } else {
            synchronized (BIMateFactory.class) {
                if (instance == null) {
                    instance = new BIMateFactory();
                }
            }
        }
        return instance;
    }


}