package com.finebi.cube.repository;

import com.finebi.common.resource.ResourceItem;
import com.finebi.common.resource.ResourceName;

/**
 * This class created on 2017/6/21.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

public interface ModifyStrategy<Name extends ResourceName, Item extends ResourceItem> {
    boolean checkAddCondition(Name name, Item item);

    boolean checkDeleteCondition(Name name);

    boolean checkUpdateCondition(Name name, Item item);

    boolean checkMerge(ResourceRepository repository);
}
