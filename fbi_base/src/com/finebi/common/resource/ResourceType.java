package com.finebi.common.resource;
/**
 * This class created on 2017/6/27.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.common.name.Name;
import com.finebi.common.name.NameImp;
import com.finebi.common.name.NameProvider;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;

public class ResourceType extends NameImp implements Name {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(ResourceType.class);

    public ResourceType(String selfNameValue, NameProvider parentName) {
        super(selfNameValue, parentName);
    }

    public ResourceType(String selfNameValue) {
        super(selfNameValue);
    }

}
