package com.fr.swift.segment.recover;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RedisBackupResultSet implements SwiftResultSet {

    private SwiftMetaData meta;
    private long cursor = -1;
    private long rowCount;
    private Segment segment;

    private RedisClient redisClient;

    public RedisBackupResultSet(Segment segment) {
        this.meta = segment.getMetaData();
        this.segment = segment;
        this.redisClient = (RedisClient) SwiftContext.getInstance().getBean("redisClient");
        this.rowCount = redisClient.llen(this.segment.getLocation().getPath());
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return meta;
    }

    @Override
    public boolean next() throws SQLException {
        cursor++;
        if (cursor < rowCount) {
            return true;
        }
        return false;
    }

    @Override
    public Row getRowData() throws SQLException {
        List<String> dataList = redisClient.lrange(this.segment.getLocation().getPath(), cursor, cursor + 1);
        if (dataList == null || dataList.isEmpty()) {
            throw new SQLException("Redis lrange data from " + this.segment.getLocation().getPath() + " error! (cursor =" + cursor + ")");
        }
        String dataJson = dataList.get(0);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Row row = objectMapper.readValue(dataJson, ListBasedRow.class);
            return row;
        } catch (IOException e) {
            throw new SQLException("Jackson readValue " + dataJson + " error!", e);
        }
    }

    @Override
    public void close() throws SQLException {
    }
}
