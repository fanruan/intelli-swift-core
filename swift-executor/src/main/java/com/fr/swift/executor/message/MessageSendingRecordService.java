package com.fr.swift.executor.message;

import com.fr.swift.executor.message.MessageSendingRecordEntity;

import java.sql.SQLException;

/**
 * @author xiqiu
 * @date 2020/6/9
 * @description
 * @since swift 1.1
 */
public interface MessageSendingRecordService {

    void save(final MessageSendingRecordEntity messageSendingRecordEntity) throws SQLException;

    void update(final MessageSendingRecordEntity messageSendingRecordEntity) throws SQLException;

}
