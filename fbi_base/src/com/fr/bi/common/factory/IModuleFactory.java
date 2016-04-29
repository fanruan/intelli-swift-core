package com.fr.bi.common.factory;

/**
 * This class created on 2016/3/14.
 *
 * @author Connery
 * @since 4.0
 */
public interface IModuleFactory extends IFactoryGetterService {
    String CONF_MODULE = "CONF_MODULE";
    String CUBE_BASE_MODULE = "CUBE_BASE_MODULE";
    String CUBE_BUILD_MODULE = "CUBE_BUILD_MODULE";

    boolean useFactory(String tag);

    void registerClass(String moduleTag, String name, Class clazz) throws BIFactoryKeyDuplicateException, BIFactoryKeyAbsentException;

    void registerObject(String moduleTag, String name, Object object) throws BIClassNonsupportException, BIFactoryKeyAbsentException;
}
