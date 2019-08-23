package com.fr.swift.exception.queue;

import com.fr.swift.SwiftContext;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.service.ExceptionInfoService;
import com.fr.swift.log.SwiftLoggers;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Marvin
 * @date 8/8/2019
 * @description
 * @since swift 1.1
 */
public class MasterExceptionInfoQueue implements ExceptionInfoQueue {

    private static final MasterExceptionInfoQueue INSTANCE = new MasterExceptionInfoQueue();

    public static MasterExceptionInfoQueue getInstance() {
        return INSTANCE;
    }

    private BlockingQueue<ExceptionInfo> queue = new ArrayBlockingQueue<>(10000);

    private ExceptionInfoService infoService = SwiftContext.get().getBean(ExceptionInfoService.class);

    private MasterExceptionInfoQueue() {
    }

    @Override
    public boolean offer(ExceptionInfo info) {
        if (infoService.existsException(info)) {
            SwiftLoggers.getLogger().info("Exception has exists");
            return false;
        }
        return queue.offer(info);
    }

    @Override
    public ExceptionInfo take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public void initExceptionInfoQueue() {
        //master队列初始化时会找出State为UNSOLVED的异常信息加入队列
        Set<ExceptionInfo> infoSet = infoService.getUnsolvedExceptionInfo();
        for (ExceptionInfo info : infoSet) {
            if (!queue.offer(info)) {
                SwiftLoggers.getLogger().warn("Add ExceptionInfo into MasterExceptionInfoQueue Failed");
            }
        }
    }
}