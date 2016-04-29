package com.finebi.cube.router;

import com.finebi.cube.exception.BIMessageFailureException;
import com.finebi.cube.message.IMessage;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface IMessageDeliver {
    /**
     * 传递消息
     *
     * @param message 消息
     */
    void deliverMessage(IMessage message) throws BIMessageFailureException;
}
