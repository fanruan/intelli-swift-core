package com.fr.bi.common.factory;


/**
 * Created by Connery on 2015/12/7.
 */
public interface IFactoryService extends IFactoryGetterService {

    String CONF_DB = "CONF_DB";
    String CONF_XML = "CONF_XML";
    String CUBE_BASE = "CUBE_BASE";
    String CUBE_BASIC_BUILD = "CUBE_BASIC_BUILD";


    void registerClass(String name, Class clazz) throws BIFactoryKeyDuplicateException;

    void registerObject(String name, Object object) throws BIClassNonsupportException;

}