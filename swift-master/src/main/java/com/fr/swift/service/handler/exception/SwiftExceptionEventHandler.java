package com.fr.swift.service.handler.exception;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.event.base.AbstractExceptionRpcEvent;
import com.fr.swift.exception.ExceptionInfo;
import com.fr.swift.exception.event.ExceptionStateRpcEvent;
import com.fr.swift.exception.process.OperateClusterSelector;
import com.fr.swift.exception.queue.MasterExceptionInfoQueue;
import com.fr.swift.service.handler.base.AbstractHandler;

import java.io.Serializable;

/**
 * @author Marvin
 * @date 8/8/2019
 * @description
 * @since swift 1.1
 */
@SwiftBean
public class SwiftExceptionEventHandler extends AbstractHandler<AbstractExceptionRpcEvent> {

    /**
     * master异常处理的具体实现
     *
     * @param event
     * @return
     * @throws Throwable
     */
    @Override
    public <S extends Serializable> S handle(AbstractExceptionRpcEvent event) throws Exception {
        switch (event.subEvent()) {
            case UNSOLVED:
                setOccupiedNode((ExceptionStateRpcEvent) event);
                masterGetException(event);
                return null;
            case SOLVED:
                freeNode((ExceptionStateRpcEvent) event);
                return null;
            default:
                throw new IllegalStateException("Unexpected value: " + event.subEvent());
        }
    }

    void masterGetException(AbstractExceptionRpcEvent event) {
        ExceptionInfo content = (ExceptionInfo) event.getContent();
        MasterExceptionInfoQueue.getInstance().offer(content);
    }

    void setOccupiedNode(ExceptionStateRpcEvent event) {
        OperateClusterSelector.getOccupiedNodes().add(event.getContent().getSourceNodeId());
        OperateClusterSelector.getOccupiedNodes().add(event.getContent().getOperateNodeId());
    }

    void freeNode(ExceptionStateRpcEvent event) {
        OperateClusterSelector.getOccupiedNodes().remove(event.getContent().getSourceNodeId());
        OperateClusterSelector.getOccupiedNodes().remove(event.getContent().getOperateNodeId());
    }
}
