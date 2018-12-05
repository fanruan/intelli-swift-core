package com.fr.swift.jdbc.proxy.invoke;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.jdbc.emb.EmbJdbcConnector;
import com.fr.swift.jdbc.mode.Mode;
import com.fr.swift.jdbc.result.SwiftPaginationResultSet;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/26
 */
public class JdbcCaller implements TableService {

    protected String address;
    protected Mode mode;

    public static SelectJdbcCaller connectSelectService(String address, Mode proxy) {
        return new SelectJdbcCaller(address, proxy);
    }

    public static SelectJdbcCaller connectSelectService(String host, int port, Mode proxy) {
        return new SelectJdbcCaller(host, port, proxy);
    }

    public static MaintenanceJdbcCaller connectMaintenanceService(String address, Mode proxy) {
        return new MaintenanceJdbcCaller(address, proxy);
    }

    public static MaintenanceJdbcCaller connectMaintenanceService(String host, int port, Mode proxy) {
        return new MaintenanceJdbcCaller(host, port, proxy);
    }

    protected <T> T invoke(Class proxyClass, Method method, Object... args) throws Exception {
        switch (mode) {
            case SERVER:
                ClientProxy proxy = null;
                ClientProxyPool pool = ClientProxyPool.getInstance(mode);
                try {
                    proxy = pool.borrowObject(address);
                    return (T) method.invoke(proxy.getProxy(proxyClass), args);
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                    pool.invalidateObject(address, proxy);
                    return null;
                } finally {
                    pool.returnObject(address, proxy);
                }
            case EMB:
                ClientProxy emb = new ClientProxy(new SimpleExecutor(new EmbJdbcConnector()));
                try {
                    emb.start();
                    return (T) method.invoke(emb.getProxy(proxyClass), args);
                } finally {
                    emb.stop();
                }
            default:
                return null;
        }

    }

    private SwiftMetaData getMetaData(SwiftDatabase schema, String tableName) throws Exception {
        Method method = TableService.class.getDeclaredMethod("detectiveMetaData", SwiftDatabase.class, String.class);
        return invoke(TableService.class, method, schema, tableName);
    }

    @Override
    public SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) {
        try {
            return getMetaData(schema, tableName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> detectiveAllTableNames(SwiftDatabase schema) {
        try {
            Method method = TableService.class.getDeclaredMethod("detectiveAllTableNames", SwiftDatabase.class);
            return invoke(TableService.class, method, schema);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public int createTable(SwiftDatabase schema, String tableName, List<Column> columns) throws Exception {
        Method method = TableService.class.getDeclaredMethod("createTable", SwiftDatabase.class, String.class, List.class);
        return invoke(TableService.class, method, schema, tableName, columns);
    }

    @Override
    public void dropTable(SwiftDatabase schema, String tableName) throws Exception {
        Method method = TableService.class.getDeclaredMethod("dropTable", SwiftDatabase.class, String.class);
        invoke(TableService.class, method, schema, tableName);
    }

    @Override
    public void truncateTable(SwiftDatabase schema, String tableName) throws Exception {
        Method method = TableService.class.getDeclaredMethod("truncateTable", SwiftDatabase.class, String.class);
        invoke(TableService.class, method, schema, tableName);
    }

    @Override
    public boolean addColumn(SwiftDatabase schema, String tableName, Column column) throws Exception {
        Method method = TableService.class.getDeclaredMethod("addColumn", SwiftDatabase.class, String.class, Column.class);
        return invoke(TableService.class, method, schema, tableName, column);
    }

    @Override
    public boolean dropColumn(SwiftDatabase schema, String tableName, String columnName) throws Exception {
        Method method = TableService.class.getDeclaredMethod("dropColumn", SwiftDatabase.class, String.class, String.class);
        return invoke(TableService.class, method, schema, tableName, columnName);
    }

    @Override
    public boolean isTableExists(SwiftDatabase schema, String tableName) {
        return null != detectiveMetaData(schema, tableName);
    }

    public static class SelectJdbcCaller extends JdbcCaller implements SelectService, TableService {
        private SelectJdbcCaller(String address, Mode proxy) {
            this.address = address;
            this.mode = proxy;
        }

        private SelectJdbcCaller(String host, int port, Mode proxy) {
            this(host + ":" + port, proxy);
        }

        @Override
        public SwiftResultSet query(SwiftDatabase database, String queryJson) throws Exception {
            Method method = SelectService.class.getDeclaredMethod("query", SwiftDatabase.class, String.class);
            SwiftResultSet resultSet = invoke(SelectService.class, method, database, queryJson);
            if (resultSet instanceof SerializableDetailResultSet) {
                return new SwiftPaginationResultSet((SerializableDetailResultSet) resultSet, this, database);
            }
            return resultSet;
        }
    }

    public static class MaintenanceJdbcCaller extends JdbcCaller implements DataMaintenanceService, TableService {
        private MaintenanceJdbcCaller(String address, Mode proxy) {
            this.address = address;
            this.mode = proxy;
        }

        private MaintenanceJdbcCaller(String host, int port, Mode proxy) {
            this(host + ":" + port, proxy);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws Exception {
            Method method = DataMaintenanceService.class.getDeclaredMethod("insert", SwiftDatabase.class, String.class, List.class, List.class);
            return invoke(SelectService.class, method, schema, tableName, fields, rows);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws Exception {
            return insert(schema, tableName, Collections.<String>emptyList(), rows);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, String queryJson) throws Exception {
            Method method = DataMaintenanceService.class.getDeclaredMethod("insert", SwiftDatabase.class, String.class, String.class);
            return invoke(SelectService.class, method, schema, tableName, queryJson);
        }

        @Override
        public int delete(SwiftDatabase schema, String tableName, Where where) throws Exception {
            Method method = DataMaintenanceService.class.getDeclaredMethod("delete", SwiftDatabase.class, String.class, Where.class);
            return invoke(SelectService.class, method, schema, tableName, where);
        }

        @Override
        public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) throws Exception {
            Method method = DataMaintenanceService.class.getDeclaredMethod("update", SwiftDatabase.class, String.class, SwiftResultSet.class, Where.class);
            return invoke(SelectService.class, method, schema, tableName, resultSet, where);
        }
    }
}