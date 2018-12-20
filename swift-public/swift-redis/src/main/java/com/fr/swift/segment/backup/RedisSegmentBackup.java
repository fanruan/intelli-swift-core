package com.fr.swift.segment.backup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.SwiftContext;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.redis.RedisClientPipeline;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.transaction.RedisTransactionManager;
import com.fr.swift.transaction.TransactionManager;

import java.util.List;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RedisSegmentBackup implements SwiftSegmentBackup {
    private Segment segment;

    protected TransactionManager transactionManager;

    protected RedisClient redisClient = SwiftContext.get().getBean(RedisClient.class);

    public RedisSegmentBackup(Segment segment, Segment currentSegment) {
        this(segment, currentSegment, segment.getMetaData().getFieldNames());
    }

    public RedisSegmentBackup(Segment segment, Segment currentSegment, List<String> fields) {
        this.segment = segment;
        transactionManager = (TransactionManager) SwiftContext.get().getBean("transactionManager");
        transactionManager.setOldAttach(currentSegment);

    }

    @Override
    public void backupRowData(int cursor, Row rowData) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(rowData);
        } catch (JsonProcessingException e) {
            SwiftLoggers.getLogger().error(e);
        }
        RedisClientPipeline pipeline = ((RedisTransactionManager) transactionManager).getRedisPipeline();
        pipeline.rPush(segment.getLocation().getPath(), json);
    }

    @Override
    public void backupNullIndex() {
    }

    @Override
    public void backupSegmentInfo(int lastCursor, int cursor) {
    }

    @Override
    public void release() {
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}
