package com.fr.swift.executor.message;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;

import java.sql.SQLException;
import java.util.List;

/**
 * @author xiqiu
 * @date 2020/6/19
 * @description
 * @since swift 1.1
 */
@SwiftBean(name = "messageSendingRecordService")
public class MessageSendingRecordServiceImpl implements MessageSendingRecordService {
    private SwiftDao dao = new SwiftDaoImpl(MessageSendingRecordEntity.class);


    @Override
    public void saveOrUpdate(MessageSendingRecordEntity messageSendingRecordEntity) throws SQLException {
        try {
            dao.insert(messageSendingRecordEntity);
        } catch (Exception e) {
            dao.update(messageSendingRecordEntity);
        }
    }

    @Override
    public void update(MessageSendingRecordEntity messageSendingRecordEntity) throws SQLException {
        dao.update(messageSendingRecordEntity);

    }

    @Override
    public List<MessageSendingRecordEntity> getById(String messageId) throws SQLException {
        List<MessageSendingRecordEntity> entities = dao.selectQuery((query, builder, from) ->
                query.select(from)
                        .where(builder.equal(from.get("messageId"), messageId)
                        ));
        return entities;
    }

    @Override
    public List<MessageSendingRecordEntity> getUnfinishedEntity() throws SQLException {
        // java 和 python time 任何一个为0，即为未完成的消息
        List<MessageSendingRecordEntity> entities = dao.selectQuery((query, builder, from) ->
                query.select(from).where(builder.or(builder.equal(from.get("pythonTime"), 0),
                        builder.equal(from.get("javaTime"), 0)
                )));
        return entities;
    }
}
