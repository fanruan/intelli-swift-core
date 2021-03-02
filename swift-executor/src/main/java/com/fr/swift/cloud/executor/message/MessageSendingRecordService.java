package com.fr.swift.cloud.executor.message;

import java.sql.SQLException;
import java.util.List;

/**
 * @author xiqiu
 * @date 2020/6/9
 * @description
 * @since swift 1.1
 */
public interface MessageSendingRecordService {

    void saveOrUpdate(final MessageSendingRecordEntity messageSendingRecordEntity) throws SQLException;

    void update(final MessageSendingRecordEntity messageSendingRecordEntity) throws SQLException;

    List<MessageSendingRecordEntity> getById(String messageId) throws SQLException;

    List<MessageSendingRecordEntity> getUnfinishedEntity() throws SQLException;

}
