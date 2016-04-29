package com.fr.bi.common.factory;

import com.fr.bi.common.factory.annotation.BIMandatedObject;

/**
 * Created by Connery on 2015/12/8.
 */
@BIMandatedObject(factory = IFactoryService.CONF_DB, implement = BIRegister4TestBasic.class)
public class BIRegister4TestTwo extends BIRegister4TestBasic {
}