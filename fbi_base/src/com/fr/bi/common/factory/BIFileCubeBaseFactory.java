package com.fr.bi.common.factory;

import com.fr.bi.common.factory.annotation.BISingletonObject;

/**
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
@BISingletonObject
public class BIFileCubeBaseFactory extends BIBeanFactory {
    @Override
    public String getFactoryTag() {
        return IFactoryService.CUBE_BASE;
    }
}