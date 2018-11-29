package com.fr.swift.segment.backup;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.ResourceDiscovery;
import com.fr.swift.redis.RedisClient;
import com.fr.swift.segment.Incrementer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.recover.RedisSegmentRecovery;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * This class created on 2018/6/22
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class RedisBackupAndRevoceryTest extends TestCase {
    private RedisClient redisClient;

    @Override
    public void setUp() throws Exception {
        SwiftContext.init();
        redisClient = (RedisClient) SwiftContext.get().getBean("redisClient");
    }

    @Ignore
    @Test
    public void testBackup() {
        try {
            redisClient.flushDB();
            DataSource dataSource = new QueryDBSource("select * from DEMO_HR_USER", "RealSwiftInserterTest");
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();
            Incrementer incrementer = new Incrementer(dataSource);
            incrementer.insertData(resultSet);

            resultSet = transfer.createResultSet();
            List<String> jsonList = new ArrayList<String>();
            while (resultSet.hasNext()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String json = null;
                try {
                    json = objectMapper.writeValueAsString(resultSet.getNextRow());
                    jsonList.add(json);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            assertEquals(redisClient.llen(String.format("%s/7bc94acd/seg0", dataSource.getMetadata().getSwiftDatabase().getBackupDir())), jsonList.size());
            List<String> redisList = redisClient.lrange(String.format("%s/7bc94acd/seg0", dataSource.getMetadata().getSwiftDatabase().getBackupDir()), 0, -1);
            assertEquals(redisList.size(), jsonList.size());
            for (int i = 0; i < redisList.size(); i++) {
                assertEquals(redisList.get(i), jsonList.get(i));
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Ignore
    @Test
    public void testRevover() {
        try {
            redisClient.flushDB();
            DataSource dataSource = new QueryDBSource("select * from DEMO_HR_USER", "RealSwiftInserterTest");
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
            SwiftResultSet resultSet = transfer.createResultSet();
            Incrementer incrementer = new Incrementer(dataSource);
            incrementer.insertData(resultSet);
            SwiftSegmentManager localSegmentProvider = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

            //释放memio，再测恢复
            ResourceDiscovery.getInstance().removeCubeResource("cubes/" + dataSource.getSourceKey().getId());

            RedisSegmentRecovery redisSegmentRecovery = new RedisSegmentRecovery();
            redisSegmentRecovery.recover(dataSource.getSourceKey());
            Segment segment = localSegmentProvider.getSegment(dataSource.getSourceKey()).get(0);
            assertEquals(segment.getRowCount(), 42);
            assertTrue(segment.getAllShowIndex().contains(0));
            assertTrue(segment.getAllShowIndex().contains(41));

            resultSet = transfer.createResultSet();

            List<Object>[] rowList = new ArrayList[segment.getMetaData().getColumnCount()];
            while (resultSet.hasNext()) {
                Row row = resultSet.getNextRow();
                for (int i = 0; i < row.getSize(); i++) {
                    try {
                        rowList[i].add(row.getValue(i));
                    } catch (Exception e) {
                        rowList[i] = new ArrayList<Object>();
                        rowList[i].add(row.getValue(i));
                    }
                }
            }

            for (int i = 1; i <= segment.getMetaData().getColumnCount(); i++) {
                String columnName = segment.getMetaData().getColumnName(i);
                Column column = segment.getColumn(new ColumnKey(columnName));
                for (int r = 0; r < 42; r++) {
                    assertEquals(rowList[i - 1].get(r), column.getDetailColumn().get(r));
                }
            }
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
