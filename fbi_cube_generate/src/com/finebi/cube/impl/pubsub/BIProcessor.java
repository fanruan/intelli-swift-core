package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.message.IMessageBody;
import com.finebi.cube.pubsub.IProcessor;
import com.finebi.cube.pubsub.IPublish;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.*;


/**
 * This class created on 2016/3/23.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIProcessor<T> implements IProcessor<Future<T>> {
    protected IPublish messagePublish;
    protected ExecutorService executorService = BIProcessorThreadManager.getInstance().getExecutorService();
    private BlockingQueue<Future<T>> resultQueue = new ArrayBlockingQueue<Future<T>>(1);

    @Override
    public void setPublish(IPublish publish) {
        messagePublish = publish;
    }

    @Override
    public void process() {
        try {
            messagePublish.publicRunningMessage(getRunningMess());
            Future<T> result = executorService.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    try {
                        T result = mainTask();
                        messagePublish.publicFinishMessage(getFinishMess());
                        return result;

                    } catch (Exception e) {
                        messagePublish.publicStopMessage(getStopMess());
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                    return null;
                }
            });
            resultQueue.add(result);
        } catch (BIDeliverFailureException e) {
            try {
                messagePublish.publicStopMessage(getStopMess());
            } catch (BIDeliverFailureException e1) {
                throw BINonValueUtils.beyondControl(e1);
            }

        }

    }

    protected IMessageBody getRunningMess() {
        return null;
    }

    protected IMessageBody getFinishMess() {
        return null;
    }

    protected IMessageBody getStopMess() {
        return null;
    }

    /**
     * TODO 捕获异常，发送终止消息，停止其他监听对象的等待。
     *
     * @return
     */
    public abstract T mainTask();

    @Override
    public Future<T> getResult() {
        try {
            return resultQueue.take();
        } catch (InterruptedException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
