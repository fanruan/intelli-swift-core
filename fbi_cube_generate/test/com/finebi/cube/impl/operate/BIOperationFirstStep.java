package com.finebi.cube.impl.operate;

import com.finebi.cube.impl.router.topic.BITopicID;
import com.finebi.cube.impl.router.topic.BITopicTag;
import com.finebi.cube.impl.router.topic.BITopicTagTestTool;
import com.finebi.cube.operate.IOperationID;
import com.finebi.cube.router.fragment.IFragmentID;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BIOperationFirstStep extends BIOperationBase {
    public BIOperationFirstStep(IOperationID operationID, IFragmentID fragmentID) {
        super(operationID, fragmentID);
        try {
            operation.subscribe(new BITopicTag(new BITopicID("Start")));
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }

    @Override
    protected Integer processBase() {
        try {
            publish.publicRunningMessage(null);
            System.out.println("Start First Step," + operation.getOperationFragmentTag());
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
            System.out.println("Start First Step," + operation.getOperationFragmentTag());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        return 0;
    }

    @Override
    protected ITopicTag generateTopicTag() {
        return BITopicTagTestTool.getTopicTagFirst();
    }


}
