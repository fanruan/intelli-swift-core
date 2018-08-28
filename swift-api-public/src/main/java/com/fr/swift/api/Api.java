package com.fr.swift.api;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.api.rpc.invoke.ApiProxyFactory;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/24
 */
public class Api implements TableService {
    protected String address;
    public static final int DEFAULT_MAX_FRAME_SIZE = 1000000000;
    protected int maxFrameSize;

    private Api(String address, int maxFrameSize) {
        this.address = address;
        this.maxFrameSize = maxFrameSize;
    }

    public static SelectApi connectSelectApi(String address, int maxFrameSize) {
        return new SelectApi(address, maxFrameSize);
    }

    public static DataMaintenanceApi connectDataMaintenanceApi(String address, int maxFrameSize) {
        return new DataMaintenanceApi(address, maxFrameSize);
    }

    public static SelectApi connectSelectApi(String address) {
        return new SelectApi(address, DEFAULT_MAX_FRAME_SIZE);
    }

    public static DataMaintenanceApi connectDataMaintenanceApi(String address) {
        return new DataMaintenanceApi(address, DEFAULT_MAX_FRAME_SIZE);
    }

    @Override
    public SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException {
        TableService service = ApiProxyFactory.getProxy(TableService.class, address, maxFrameSize);
        return service.detectiveMetaData(schema, tableName);
    }

    @Override
    public boolean isTableExists(SwiftDatabase schema, String tableName) {
        TableService service = ApiProxyFactory.getProxy(TableService.class, address, maxFrameSize);
        return service.isTableExists(schema, tableName);
    }

    public static class SelectApi extends Api implements SelectService {

        private SelectApi(String address, int maxFrameSize) {
            super(address, maxFrameSize);
        }

        @Override
        public SwiftResultSet query(SwiftDatabase database, String queryJson) {
            SelectService service = ApiProxyFactory.getProxy(SelectService.class, address, maxFrameSize);
            return service.query(database, queryJson);
        }
    }

    public static class DataMaintenanceApi extends Api implements DataMaintenanceService {

        private DataMaintenanceApi(String address, int maxFrameSize) {
            super(address, maxFrameSize);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws SQLException {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address, maxFrameSize);
            return service.insert(schema, tableName, fields, rows);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws SQLException {
            return insert(schema, tableName, Collections.<String>emptyList(), rows);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, String queryJson) throws SQLException {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address, maxFrameSize);
            return service.insert(schema, tableName, queryJson);
        }

        @Override
        public int delete(SwiftDatabase schema, String tableName, Where where) {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address, maxFrameSize);
            return service.delete(schema, tableName, where);
        }

        @Override
        public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address, maxFrameSize);
            return service.update(schema, tableName, resultSet, where);
        }

        @Override
        public int createTable(SwiftDatabase schema, String tableName, List<Column> columns) {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address, maxFrameSize);
            return service.createTable(schema, tableName, columns);
        }
    }


}
