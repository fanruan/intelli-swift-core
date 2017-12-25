package com.finebi.base.common.resource;


import com.finebi.common.name.NameProvider;

/**
 * This class created on 2017/4/10.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface ResourceItem extends NameProvider, Cloneable {

    /**
     * 资源名字
     *
     * @return
     */
    ResourceName getResourceName();

    /**
     * 复制
     *
     * @return
     * @throws CloneNotSupportedException
     */
    Object clone() throws CloneNotSupportedException;

    /**
     * 版本
     *
     * @return
     */
    long version();
}
