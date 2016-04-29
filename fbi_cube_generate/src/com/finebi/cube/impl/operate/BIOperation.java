package com.finebi.cube.impl.operate;

import com.finebi.cube.exception.BIFragmentAbsentException;
import com.finebi.cube.exception.BIRegisterIsForbiddenException;
import com.finebi.cube.exception.BIStatusAbsentException;
import com.finebi.cube.exception.BITopicAbsentException;
import com.finebi.cube.gen.mes.BICubeBuildFragmentTag;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.impl.pubsub.BIPublishID;
import com.finebi.cube.impl.pubsub.BISubscribeID;
import com.finebi.cube.operate.IOperation;
import com.finebi.cube.operate.IOperationID;
import com.finebi.cube.pubsub.*;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.common.factory.BIMateFactory;
import com.fr.bi.common.factory.IModuleFactory;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.utils.program.BINonValueUtils;

/**
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
@BIMandatedObject(module = IModuleFactory.CUBE_BUILD_MODULE, factory = BIMateFactory.CUBE_BASIC_BUILD
        , implement = IOperation.class)
public class BIOperation<R> implements IOperation<R> {
    protected ITopicTag operationTopicTag;
    protected IFragmentTag operationFragmentTag;
    protected ISubscribe preconditionSubscribe;
    protected IPublish messagePublish;
    protected IProcessor<R> operationProcessor;
    protected IOperationID operationID;

    public BIOperation(IOperationID operationID, IProcessor processor) {
        BINonValueUtils.checkNull(operationID, processor);
        this.operationProcessor = processor;
        this.operationID = operationID;
        IPublishID publishID = new BIPublishID(operationID.getIdentityValue());
        this.messagePublish = BIFactoryHelper.getObject(IPublish.class, publishID);
        this.operationProcessor.setPublish(messagePublish);
        ISubscribeID subscribeID = new BISubscribeID(operationID.getIdentityValue());
        this.preconditionSubscribe = BIFactoryHelper.getObject(ISubscribe.class, subscribeID, this.operationProcessor);
        initMessageOccupied();
    }

    private void initMessageOccupied() {
        setOperationTopicTag(BICubeBuildTopicTag.BI_CUBE_OCCUPIED_TOPIC);
        setOperationFragmentTag(BICubeBuildFragmentTag.getCubeOccupiedFragment(BICubeBuildTopicTag.BI_CUBE_OCCUPIED_TOPIC));
    }

    public BIOperation(String operationIDValue, IProcessor processor) {
        this(new BIOperationID(operationIDValue), processor);
    }

    @Override
    public void setOperationTopicTag(ITopicTag topicTag) {
        operationTopicTag = topicTag;
        messagePublish.setTopicTag(topicTag);

    }

    @Override
    public void setOperationFragmentTag(IFragmentTag fragmentTag) {
        operationFragmentTag = fragmentTag;
        messagePublish.setFragmentTag(fragmentTag);
    }

    @Override
    public ITopicTag getOperationTopicTag() {
        return this.operationTopicTag;
    }

    @Override
    public IFragmentTag getOperationFragmentTag() {
        return this.operationFragmentTag;
    }

    @Override
    public void subscribe(IFragmentTag fragmentTag) throws BITopicAbsentException,
            BIFragmentAbsentException, BIRegisterIsForbiddenException {
        this.preconditionSubscribe.subscribe(fragmentTag);
    }

    @Override
    public void subscribe(IStatusTag statusTag) throws BITopicAbsentException,
            BIFragmentAbsentException, BIStatusAbsentException, BIRegisterIsForbiddenException {
        this.preconditionSubscribe.subscribe(statusTag);
    }

    @Override
    public void subscribe(ITopicTag topicTag) throws BITopicAbsentException, BIRegisterIsForbiddenException {
        this.preconditionSubscribe.subscribe(topicTag);
    }

    @Override
    public R getOperationResult() {
        return operationProcessor.getResult();
    }

    @Override
    public boolean isSubscribed(ITopicTag topicTag) throws BITopicAbsentException {
        return preconditionSubscribe.isSubscribed(topicTag);
    }

    @Override
    public boolean isSubscribed(IStatusTag statusTag) throws BITopicAbsentException, BIFragmentAbsentException, BIStatusAbsentException {
        return preconditionSubscribe.isSubscribed(statusTag);
    }

    @Override
    public boolean isSubscribed(IFragmentTag fragmentTag) throws BITopicAbsentException, BIFragmentAbsentException {
        return preconditionSubscribe.isSubscribed(fragmentTag);
    }
}
