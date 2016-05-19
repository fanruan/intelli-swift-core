package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.BIDeliverFailureException;
import com.finebi.cube.message.IMessage;
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

    private void showProcess(final IMessage lastReceiveMessage) {
        BILogger.getLogger().debug("Process:" + messagePublish + "\n" + "        Get message:" + lastReceiveMessage+"\n");
    }

    @Override
    public void process(final IMessage lastReceiveMessage) {
        showProcess(lastReceiveMessage);
        if (lastReceiveMessage.isStopStatus()) {
            try {
                messagePublish.publicStopMessage(getStopMess());
            } catch (BIDeliverFailureException e) {
                throw BINonValueUtils.beyondControl(e);
            }
        }
        try {
            messagePublish.publicRunningMessage(getRunningMess());
            Future<T> result = executorService.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    try {
                        T result = mainTask(lastReceiveMessage);
                        release();
                        messagePublish.publicFinishMessage(getFinishMess());
                        return result;
                    } catch (Exception e) {
                        messagePublish.publicStopMessage(getStopMess());
                        BILogger.getLogger().error(e.getMessage(), e);
                        release();
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
     * @param lastReceiveMessage
     * @return
     */
    public abstract T mainTask(IMessage lastReceiveMessage);


    public abstract void release();

    @Override
    public Future<T> getResult() {
        try {
            return resultQueue.take();
        } catch (InterruptedException e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }
}
