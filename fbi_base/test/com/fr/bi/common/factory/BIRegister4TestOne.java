package com.fr.bi.common.factory;

import com.fr.bi.common.factory.annotation.BIMandatedObject;

/**
 * Created by Connery on 2015/12/8.
 */
@BIMandatedObject(factory = IFactoryService.CONF_XML, key = "one", implement = BIRegister4TestOne.class)
public class BIRegister4TestOne extends BIRegister4TestBasic {
}