package com.finebi.cube.gen.oper.observer;

import com.finebi.cube.gen.mes.BICubeBuildFragmentTag;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.mes.BIStatusUtils;
import com.finebi.cube.impl.operate.BIOperation;
import com.finebi.cube.operate.IOperationID;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/4/20.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeFinishObserver<R> extends BIOperation<R> {
    private FinishObserverProcessor processor;

    public BICubeFinishObserver(IOperationID operationID, FinishObserverProcessor processor) {
        super(operationID, processor);
        this.processor = processor;
        ITopicTag topicTag = BICubeBuildTopicTag.FINISH_BUILD_CUBE;
        IFragmentTag fragmentTag = BICubeBuildFragmentTag.getCubeOccupiedFragment(topicTag);
        try {
            this.subscribe(BIStatusUtils.generateStatusFinish(topicTag, fragmentTag.getFragmentID().getIdentityValue()));
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public BICubeFinishObserver(IOperationID operationID) {
        this(operationID, new FinishObserverProcessor());

    }

    public boolean success() {
        return processor.success();
    }
}
