package com.fr.swift.api;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.api.rpc.invoke.ApiProxyFactory;
import com.fr.swift.db.Schema;
import com.fr.swift.db.Where;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/24
 */
public class Api {
    protected String address;

    private Api(String address) {
        this.address = address;
    }

    public static SelectApi connectSelectApi(String address) {
        return new SelectApi(address);
    }

    public static DataMaintenanceApi connectDataMaintenanceApi(String address) {
        return new DataMaintenanceApi(address);
    }

    public static class SelectApi extends Api implements SelectService {

        private SelectApi(String address) {
            super(address);
        }

        @Override
        public SwiftResultSet query(String queryJson) {
            SelectService service = ApiProxyFactory.getProxy(SelectService.class, address);
            return service.query(queryJson);
        }
    }

    public static class DataMaintenanceApi extends Api implements DataMaintenanceService {

        private DataMaintenanceApi(String address) {
            super(address);
        }

        @Override
        public int insert(Schema schema, String tableName, List<String> fields, List<Row> rows) throws SQLException {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
            return service.insert(schema, tableName, fields, rows);
        }

        @Override
        public int insert(Schema schema, String tableName, List<Row> rows) throws SQLException {
            return insert(schema, tableName, Collections.<String>emptyList(), rows);
        }

        @Override
        public int insert(Schema schema, String tableName, String queryJson) throws SQLException {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
            return service.insert(schema, tableName, queryJson);
        }

        @Override
        public int delete(Schema schema, String tableName, Where where) {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
            return service.delete(schema, tableName, where);
        }

        @Override
        public int update(Schema schema, String tableName, SwiftResultSet resultSet, Where where) {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
            return service.update(schema, tableName, resultSet, where);
        }

        @Override
        public int createTable(Schema schema, String tableName, List<Column> columns) {
            DataMaintenanceService service = ApiProxyFactory.getProxy(DataMaintenanceService.class, address);
            return service.createTable(schema, tableName, columns);
        }
    }


}
