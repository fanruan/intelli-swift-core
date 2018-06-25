package com.fr.swift.segment.backup;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

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
    private RedisClient redisClient;

    public RedisSegmentBackup(Segment segment) {
    }

    public RedisSegmentBackup(Segment segment, List<String> fields) {
        this.segment = segment;
        this.redisClient = (RedisClient) SwiftContext.getInstance().getBean("redisClient");
    }

    @Override
    public void backupRowData(int cursor, Row rowData) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(rowData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        redisClient.rpush(segment.getLocation().getPath(), json);
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
}
