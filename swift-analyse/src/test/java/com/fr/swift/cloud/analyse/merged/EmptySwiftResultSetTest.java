package com.fr.swift.cloud.analyse.merged;

import com.fr.swift.cloud.config.entity.MetaDataColumnEntity;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.source.SwiftMetaData;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;

/**
 * @author yaohwu
 * created by yaohwu at 2021/3/5 6:59 下午
 */
public class EmptySwiftResultSetTest {

    @Test
    public void get() {
        SwiftMetaData metaData = new SwiftMetaDataEntity.Builder()
                .setSwiftSchema(SwiftDatabase.CUBE)
                .setTableName("fake-table-name")
                .addField(MetaDataColumnEntity.ofString("fake-table-column", 255))
                .build();
        BaseDetailResultSet swiftResultSet = EmptySwiftResultSet.get();
        swiftResultSet.setMetaData(metaData);
        try {
            Assert.assertSame(metaData, swiftResultSet.getMetaData());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void create() {
        SwiftMetaData metaData = new SwiftMetaDataEntity.Builder()
                .setSwiftSchema(SwiftDatabase.CUBE)
                .setTableName("fake-table-name")
                .addField(MetaDataColumnEntity.ofString("fake-table-column", 255))
                .build();
        BaseDetailResultSet swiftResultSet = EmptySwiftResultSet.create();
        swiftResultSet.setMetaData(metaData);
        try {
            Assert.assertSame(metaData, swiftResultSet.getMetaData());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getFetchSize() {
        Assert.assertEquals(0, EmptySwiftResultSet.create().getFetchSize());
    }

    @Test
    public void setMetaData() {
        SwiftMetaData metaData = new SwiftMetaDataEntity.Builder()
                .setSwiftSchema(SwiftDatabase.CUBE)
                .setTableName("fake-table-name")
                .addField(MetaDataColumnEntity.ofString("fake-table-column", 255))
                .build();
        BaseDetailResultSet swiftResultSet = EmptySwiftResultSet.create();
        swiftResultSet.setMetaData(metaData);
        try {
            Assert.assertSame(metaData, swiftResultSet.getMetaData());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getMetaData() {
        try {
            SwiftMetaData metaData = EmptySwiftResultSet.create().getMetaData();
            Assert.assertNull(metaData);
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void hasNext() {
        try {
            Assert.assertFalse(EmptySwiftResultSet.create().hasNext());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getNextRow() {
        try {
            Assert.assertNull(EmptySwiftResultSet.create().getNextRow());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void close() {
        try {
            EmptySwiftResultSet.create().close();
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }
}