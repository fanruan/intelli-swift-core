package com.finebi.cube.impl.pubsub;

import com.finebi.cube.impl.message.BIMessageTestTool;
import com.finebi.cube.impl.router.fragment.BIFragmentTagTestTool;
import com.finebi.cube.impl.router.status.BIStatusTestTool;
import com.finebi.cube.impl.router.topic.BITopicTagTestTool;
import com.fr.bi.stable.utils.code.BILogger;
import junit.framework.TestCase;

/**
 * This class created on 2016/5/4.
 *
 * @author Connery
 * @since 4.0
 */
public class TriggerThresholdTest extends TestCase {
    private BITriggerThreshold threshold;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        threshold = new BITriggerThreshold();
    }

    public void testBasicAndCondition() {
        try {
            assertFalse(threshold.isMeetThreshold());
            threshold.addAndTopic(BITopicTagTestTool.getTopicTagA());
            threshold.addAndFragment(BIFragmentTagTestTool.getFragmentTagA());
            assertFalse(threshold.isMeetThreshold());
            threshold.handleMessage(BIMessageTestTool.generateMessageTa());
            assertFalse(threshold.isMeetThreshold());
            threshold.handleMessage(BIMessageTestTool.generateMessageTaFa());
            assertTrue(threshold.isMeetThreshold());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testBasicOrCondition() {
        try {
            assertFalse(threshold.isMeetThreshold());
            threshold.addAndTopic(BITopicTagTestTool.getTopicTagA());
            threshold.addAndFragment(BIFragmentTagTestTool.getFragmentTagA());
            threshold.addOrStatus(BIStatusTestTool.generateFinishA());
            assertFalse(threshold.isMeetThreshold());
            threshold.handleMessage(BIMessageTestTool.generateMessageStatusFinish());
            assertTrue(threshold.isMeetThreshold());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
            assertTrue(false);
        }
    }

    public void testAddAnd() {
        try {
            threshold.addAndTopic(2, BITopicTagTestTool.getTopicTagA());
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testAddAndNegative() {
        try {
            threshold.addAndTopic(-1, BITopicTagTestTool.getTopicTagA());
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testAddOr() {
        try {
            assertEquals(1, threshold.conditionItemSize());
            threshold.addOrTopic(BITopicTagTestTool.getTopicTagA());
            assertEquals(2, threshold.conditionItemSize());
            threshold.addOrTopic(BITopicTagTestTool.getTopicTagA());
            assertEquals(3, threshold.conditionItemSize());
            threshold.addOrTopic(BITopicTagTestTool.getTopicTagA());
            assertEquals(4, threshold.conditionItemSize());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testThreeOrCondition() {
        try {
            assertFalse(threshold.isMeetThreshold());
            assertEquals(1, threshold.conditionItemSize());
            threshold.addOrTopic(BITopicTagTestTool.getTopicTagA());
            assertFalse(threshold.isMeetThreshold());
            assertEquals(2, threshold.conditionItemSize());
            threshold.addOrTopic(BITopicTagTestTool.getTopicTagA());
            assertEquals(3, threshold.conditionItemSize());
            threshold.addOrTopic(BITopicTagTestTool.getTopicTagA());
            assertEquals(4, threshold.conditionItemSize());
            threshold.handleMessage(BIMessageTestTool.generateMessageTa());
            assertTrue(threshold.isMeetThreshold());

        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
