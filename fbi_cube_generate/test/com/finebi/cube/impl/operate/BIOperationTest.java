package com.finebi.cube.impl.operate;

import com.finebi.cube.impl.pubsub.BIPublishID;
import com.finebi.cube.impl.router.fragment.BIFragmentTagTestTool;
import com.finebi.cube.impl.router.status.BIStatusTag;
import com.finebi.cube.impl.router.topic.BITopicTagTestTool;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusTag;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/25.
 *
 * @author Connery
 * @since 4.0
 */
public class BIOperationTest extends TestCase {

    public void testStart() {
        try {
            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            router.reset();
            router.registerTopic(BITopicTagTestTool.getTopicTagStart());

            IPublish publish = BIFactoryHelper.getObject(IPublish.class, new BIPublishID("startOperation"));

            publish.setTopicTag(BITopicTagTestTool.getTopicTagStart());
            BIOperationBase operationBase = new BIOperationFirstStep(new BIOperationID("first")
                    , BIFragmentTagTestTool.getFragmentTagTFirst_FPartOne().getFragmentID());
            BIProcessor4Test processor4Test = new BIProcessor4Test();
            BIOperation testOperation = new BIOperation(new BIOperationID("listener"), processor4Test);
            assertFalse(processor4Test.isReceiveMess());
            IFragmentTag fragmentTag = BIFragmentTagTestTool.getFragmentTagTFirst_FPartOne();
            IStatusTag startTag = BIStatusTag.getRunningStatusTag(fragmentTag);
            testOperation.subscribe(startTag);
            publish.publicMessage(null);
            Thread.sleep(4000);
            assertFalse(!processor4Test.isReceiveMess());


        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }

    public void testStartStepTwo() {
        try {

            IRouter router = BIFactoryHelper.getObject(IRouter.class);
            router.reset();
            router.registerTopic(BITopicTagTestTool.getTopicTagStart());
            IPublish startPublish = BIFactoryHelper.getObject(IPublish.class, new BIPublishID("startOperation"));
            startPublish.setTopicTag(BITopicTagTestTool.getTopicTagStart());

            IProcessor firstPartOne = new BIPrcessorStepOne();
            BIOperation firstPartOneOperation = new BIOperation(new BIOperationID("firstOne"), firstPartOne);
            firstPartOneOperation.subscribe(BITopicTagTestTool.getTopicTagStart());
            firstPartOneOperation.setOperationTopicTag(BITopicTagTestTool.getTopicTagFirst());
            firstPartOneOperation.setOperationFragmentTag(BIFragmentTagTestTool.getFragmentTagTFirst_FPartOne());

            IProcessor firstPartTwo = new BIPrcessorStepOne();
            BIOperation firstPartTwoOperation = new BIOperation(new BIOperationID("firstTwo"), firstPartTwo);
            firstPartTwoOperation.subscribe(BITopicTagTestTool.getTopicTagStart());
            firstPartTwoOperation.setOperationTopicTag(BITopicTagTestTool.getTopicTagFirst());
            firstPartTwoOperation.setOperationFragmentTag(BIFragmentTagTestTool.getFragmentTagTFirst_FPartTwo());


            IProcessor secondOne = new BIPrcessorStepTwo();
            BIOperation secondOperation = new BIOperation(new BIOperationID("secondOne"), secondOne);
            secondOperation.subscribe(BIStatusTag.getFinishStatusTag(BIFragmentTagTestTool.getFragmentTagTFirst_FPartOne()));
            secondOperation.subscribe(BIStatusTag.getFinishStatusTag(BIFragmentTagTestTool.getFragmentTagTFirst_FPartTwo()));
            secondOperation.setOperationTopicTag(BITopicTagTestTool.getTopicTagSecond());
            secondOperation.setOperationFragmentTag(BIFragmentTagTestTool.getFragmentTagTSeconde_FPartOne());
            startPublish.publicMessage(null);
            Thread.sleep(4000);
        } catch (Exception e) {
            e.printStackTrace();
            BILogger.getLogger().error(e.getMessage(), e);
            assertFalse(true);
        }
    }
}
