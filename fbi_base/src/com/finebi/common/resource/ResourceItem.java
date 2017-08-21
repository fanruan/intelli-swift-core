package com.finebi.common.resource;

import com.finebi.common.name.NameProvider;

/**
 * This class created on 2017/4/10.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public interface ResourceItem extends NameProvider, Cloneable{
    ResourceName getResourceName();

    Object clone() throws CloneNotSupportedException;

    long version();
}
