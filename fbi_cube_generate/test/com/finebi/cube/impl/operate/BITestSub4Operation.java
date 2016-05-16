package com.finebi.cube.impl.operate;

import com.finebi.cube.operate.IOperationID;
import com.finebi.cube.router.fragment.IFragmentID;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BITestSub4Operation extends BIOperationBase {
    public BITestSub4Operation(IOperationID operationID, IFragmentID fragmentID) {
        super(operationID, fragmentID);
    }

    @Override
    protected Integer processBase() {
        return null;
    }

    @Override
    protected ITopicTag generateTopicTag() {
        return null;
    }

    public void testVoid() {

    }
}
