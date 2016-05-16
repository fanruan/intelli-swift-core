package com.finebi.cube.impl.router;

import com.finebi.cube.message.IMessage;
import com.finebi.cube.router.topic.ITopicRouterService;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.concurrent.*;

/**
 * This class created on 2016/3/26.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMessageDispatcher implements Runnable {
    protected BlockingQueue<IMessage> messageQueue = new ArrayBlockingQueue<IMessage>(10240);
    protected ITopicRouterService topicRouterService;
    protected ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void setTopicRouterService(ITopicRouterService topicRouterService) {
        this.topicRouterService = topicRouterService;
    }

    public void addMessage(IMessage message) {
        synchronized (messageQueue) {
            try {
                messageQueue.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDispatched() {
        return messageQueue.isEmpty();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final IMessage message = messageQueue.take();
                executorService.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        try {
                            topicRouterService.deliverMessage(message);
                            return new Object();
                        } catch (Exception e) {
                            BILogger.getLogger().error(e.getMessage(), e);
                        }
                        return null;
                    }
                });

            } catch (InterruptedException e) {
                BILogger.getLogger().error(e.getMessage(), e);
                break;
            }
        }
    }
}
