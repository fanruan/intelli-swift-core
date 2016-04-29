package com.finebi.cube.impl.operate;

import com.finebi.cube.impl.router.fragment.BIFragmentTag;
import com.finebi.cube.operate.IOperation;
import com.finebi.cube.operate.IOperationID;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;
import com.finebi.cube.router.fragment.IFragmentID;
import com.finebi.cube.router.topic.ITopicTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIOperationBase extends TestCase {
    protected IOperation operation;
    protected IPublish publish;

    public BIOperationBase(IOperationID operationID, IFragmentID fragmentID) {
        operation = BIFactoryHelper.getObject(IOperation.class, operationID, new IProcessor<Integer>() {
            @Override
            public void process() {
                processBase();
            }

            @Override
            public Integer getResult() {
                return null;
            }

            @Override
            public void setPublish(IPublish publish) {
                BIOperationBase.this.publish = publish;
            }
        });
        operation.setOperationFragmentTag(new BIFragmentTag(fragmentID, generateTopicTag()));
        operation.setOperationTopicTag(generateTopicTag());
    }

    protected abstract Integer processBase();

    protected abstract ITopicTag generateTopicTag();


}
