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
    private BlockingQueue<IMessage> messageQueue = new ArrayBlockingQueue<IMessage>(10240);
    private ITopicRouterService topicRouterService;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

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
