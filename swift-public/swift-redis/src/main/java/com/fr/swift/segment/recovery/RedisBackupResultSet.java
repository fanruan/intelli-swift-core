package com.fr.swift.segment.recovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.SwiftContext;
import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

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
    private int cursor = 0;
    private long rowCount;
    private Segment segment;
    private ImmutableBitMap allShowIndex;
    private static final String REDIS_ALLSHOWINDEX_KEY = "/redis_allshowindex";

    private RedisClient redisClient;

    public RedisBackupResultSet(Segment segment) {
        init(segment);
    }

    private void init(Segment segment) {
        this.meta = segment.getMetaData();
        this.segment = segment;
        this.redisClient = (RedisClient) SwiftContext.get().getBean("redisClient");
        this.rowCount = redisClient.llen(this.segment.getLocation().getPath());
        String indexStr = redisClient.get(this.segment.getLocation().getPath() + REDIS_ALLSHOWINDEX_KEY);
        if (indexStr == null) {
            allShowIndex = BitMaps.newAllShowBitMap((int) rowCount);
        } else {
            IntList intList = IntListFactory.createIntList((int) rowCount);
            String[] indexArrays = indexStr.split(",");
            for (String index : indexArrays) {
                intList.add(Integer.valueOf(index));
            }
            allShowIndex = BitMaps.newImmutableBitMap(intList);
        }
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return meta;
    }

    @Override
    public boolean hasNext() {
        return cursor < rowCount;
    }

    @Override
    public Row getNextRow() throws SQLException {
        List<String> dataList = redisClient.lrange(this.segment.getLocation().getPath(), cursor, cursor + 1);
        if (dataList == null || dataList.isEmpty()) {
            throw new SQLException("Redis lrange data from " + this.segment.getLocation().getPath() + " error! (cursor =" + cursor + ")");
        }
        cursor++;
        String dataJson = dataList.get(0);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(dataJson, ListBasedRow.class);
        } catch (IOException e) {
            throw new SQLException("Jackson readValue " + dataJson + " error!", e);
        }
    }

    public ImmutableBitMap getAllShowIndex() {
        return allShowIndex;
    }

    @Override
    public void close() {
    }
}
