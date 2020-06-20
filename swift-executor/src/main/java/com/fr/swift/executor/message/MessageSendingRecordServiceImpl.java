package com.fr.swift.executor.message;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;

import java.sql.SQLException;

/**
 * @author xiqiu
 * @date 2020/6/19
 * @description
 * @since swift 1.1
 */
@SwiftBean(name = "messageSendingRecordService")
public class MessageSendingRecordServiceImpl implements MessageSendingRecordService {
    private SwiftDao dao = new SwiftDaoImpl(MessageSendingRecordServiceImpl.class);


    @Override
    public void save(MessageSendingRecordEntity messageSendingRecordEntity) throws SQLException {
        dao.insert(messageSendingRecordEntity);
    }

    @Override
    public void update(MessageSendingRecordEntity messageSendingRecordEntity) throws SQLException {
        dao.update(messageSendingRecordEntity);

    }

}
