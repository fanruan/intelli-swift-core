package com.finebi.cube.impl.router;

import com.finebi.cube.exception.BIMessageFailureException;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.topic.ITopicRouterService;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * This class created on 2016/3/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageDispatcher implements Runnable {
    private BlockingQueue<IMessage> messageQueue = new ArrayBlockingQueue<IMessage>(10240);
    private ITopicRouterService topicRouterService;

    public void setTopicRouterService(ITopicRouterService topicRouterService) {
        this.topicRouterService = topicRouterService;
    }

    public void addMessage(IMessage message) {
        synchronized (messageQueue) {
            messageQueue.add(message);
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                IMessage message = messageQueue.take();
                topicRouterService.deliverMessage(message);
            } catch (InterruptedException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                break;
            } catch (BIMessageFailureException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
    }
}
