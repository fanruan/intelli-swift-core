package com.finebi.cube.impl.router;

import com.finebi.cube.exception.BIMessageFailureException;
import com.finebi.cube.message.IMessage;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * This class created on 2016/5/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BIOneThreadDispatcher extends BIMessageDispatcher {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final IMessage message = messageQueue.take();
                try {
                    topicRouterService.deliverMessage(message);
                } catch (BIMessageFailureException e) {
                    e.printStackTrace();
                }


            } catch (InterruptedException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                break;
            }
        }
    }
}
