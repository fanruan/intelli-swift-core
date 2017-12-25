package com.finebi.base.common.resource;

import com.finebi.common.name.Name;
import com.finebi.common.name.NameImp;
import com.finebi.common.name.NameProvider;

/**
 * This class created on 2017/6/27.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */



public class ResourceType extends NameImp implements Name {

    public ResourceType(String selfNameValue, NameProvider parentName) {
        super(selfNameValue, parentName);
    }

    public ResourceType(String selfNameValue) {
        super(selfNameValue);
    }

}
