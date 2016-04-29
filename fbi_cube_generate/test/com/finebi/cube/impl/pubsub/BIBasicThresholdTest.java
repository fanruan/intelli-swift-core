package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.BIRegisterIsForbiddenException;
import com.finebi.cube.exception.BITagDuplicateException;
import com.finebi.cube.exception.BIThresholdIsOffException;
import com.finebi.cube.impl.message.BIMessageTestTool;
import com.finebi.cube.impl.router.topic.BITopicTagTestTool;
import junit.framework.TestCase;

/**
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
public class BIBasicThresholdTest extends TestCase {
    private BITopicTagThreshold threshold;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        threshold = new BITopicTagThreshold();
    }

    public void testRegister() {
        try {
            assertFalse(threshold.isSwitchOn());
            assertFalse(threshold.isRegisterClosed());
            threshold.registerThresholdTag(BITopicTagTestTool.getTopicTagA());
            assertFalse(threshold.isTriggered());
            assertTrue(threshold.isSwitchOn());
            assertFalse(threshold.isRegisterClosed());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testHandleMessage() {
        try {
            assertTrue(!threshold.isSwitchOn());
            assertTrue(!threshold.isRegisterClosed());
            threshold.registerThresholdTag(BITopicTagTestTool.getTopicTagA());
            assertTrue(!threshold.isTriggered());
            assertTrue(threshold.isSwitchOn());
            assertTrue(!threshold.isRegisterClosed());
            threshold.handleMessage(BIMessageTestTool.generateMessageTaFa());
            assertTrue(!threshold.isTriggered());
            assertTrue(threshold.isSwitchOn());
            assertTrue(threshold.isRegisterClosed());
            threshold.handleMessage(BIMessageTestTool.generateMessageTa());
            assertTrue(threshold.isTriggered());
            assertTrue(threshold.isMeetThreshold());
            assertTrue(threshold.isSwitchOn());
            assertTrue(threshold.isRegisterClosed());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testHandleMessageNoRegister() {
        try {
            assertTrue(threshold.isTriggered());
            assertTrue(!threshold.isMeetThreshold());
            assertTrue(!threshold.isSwitchOn());
            assertTrue(!threshold.isRegisterClosed());
            threshold.handleMessage(BIMessageTestTool.generateMessageTa());
            assertFalse(true);

        } catch (BIThresholdIsOffException e) {
            assertTrue(true);
            return;
        }
        assertFalse(true);
    }

    public void testRegisterHandleMessageRegister() {
        try {
            assertTrue(threshold.isTriggered());
            try {
                assertTrue(!threshold.isMeetThreshold());
            } catch (BIThresholdIsOffException e) {
                assertTrue(true);
            }
            assertTrue(!threshold.isSwitchOn());
            assertTrue(!threshold.isRegisterClosed());
            threshold.registerThresholdTag(BITopicTagTestTool.getTopicTagA());
            assertTrue(!threshold.isTriggered());
            assertTrue(!threshold.isMeetThreshold());
            assertTrue(threshold.isSwitchOn());
            assertTrue(!threshold.isRegisterClosed());
            threshold.handleMessage(BIMessageTestTool.generateMessageTa());
            assertTrue(threshold.isTriggered());
            assertTrue(threshold.isMeetThreshold());
            assertTrue(threshold.isSwitchOn());
            assertTrue(threshold.isRegisterClosed());
            threshold.registerThresholdTag(BITopicTagTestTool.getTopicTagB());
            assertFalse(true);

        } catch (BIThresholdIsOffException e) {
            assertFalse(true);
            return;
        } catch (BIRegisterIsForbiddenException e) {
            assertFalse(false);
            return;
        } catch (BITagDuplicateException e) {
            assertFalse(true);
            return;
        }

    }

    public void testRegisterHandleDiffMessage() {
        try {
            assertTrue(threshold.isTriggered());
            try {
                assertTrue(!threshold.isMeetThreshold());
            } catch (BIThresholdIsOffException e) {
                assertTrue(true);
            }
            assertTrue(!threshold.isSwitchOn());
            assertTrue(!threshold.isRegisterClosed());
            threshold.registerThresholdTag(BITopicTagTestTool.getTopicTagA());
            assertTrue(!threshold.isTriggered());
            assertTrue(!threshold.isMeetThreshold());
            assertTrue(threshold.isSwitchOn());
            assertTrue(!threshold.isRegisterClosed());
            threshold.handleMessage(BIMessageTestTool.generateMessageTb());
            assertTrue(!threshold.isTriggered());
            assertTrue(!threshold.isMeetThreshold());
            assertTrue(threshold.isSwitchOn());
            assertTrue(threshold.isRegisterClosed());
        } catch (BIThresholdIsOffException e) {
            assertFalse(true);
            return;
        } catch (BIRegisterIsForbiddenException e) {
            assertFalse(false);
            return;
        } catch (BITagDuplicateException e) {
            assertFalse(true);
            return;
        }

    }

}
