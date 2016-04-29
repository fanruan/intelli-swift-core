package com.fr.bi.common.factory;

/**
 * This class created on 2016/3/15.
 *
 * @author Connery
 * @since 4.0
 */
public interface IFactoryGetterService {
    String getFactoryTag();

    /**
     * 获得相应实例
     *
     * @param registeredName 注册的名称
     * @param parameter      构造函数的参数
     * @return 返回注册名称对应的class实例
     * @throws Exception
     */
    Object getObject(String registeredName, Object... parameter) throws Exception;

    Object getObject(String registeredName) throws Exception;
}
