package com.finebi.cube.repository;/**
 * This class created on 2017/6/21.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.common.resource.ResourceItem;
import com.finebi.common.resource.ResourceName;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;

public class DefaultModifyStrategy<Name extends ResourceName, Item extends ResourceItem>
        implements ModifyStrategy<Name, Item> {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(DefaultModifyStrategy.class);

    @Override
    public boolean checkAddCondition(Name name, Item item) {
        return true;
    }

    @Override
    public boolean checkMerge(ResourceRepository repository) {
        return true;
    }

    @Override
    public boolean checkDeleteCondition(Name name) {
        return true;
    }

    @Override
    public boolean checkUpdateCondition(Name name, Item item) {
        return true;
    }
}
