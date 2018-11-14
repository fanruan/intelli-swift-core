package com.fr.swift.jdbc.proxy.invoke;

import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.jdbc.result.SwiftPaginationResultSet;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/26
 */
public class JdbcCaller implements TableService {

    protected String address;
    protected ClientProxyPool pool;

    public static SelectJdbcCaller connectSelectService(String address, ClientProxyPool proxy) {
        return new SelectJdbcCaller(address, proxy);
    }

    public static SelectJdbcCaller connectSelectService(String host, int port, ClientProxyPool proxy) {
        return new SelectJdbcCaller(host, port, proxy);
    }

    public static MaintenanceJdbcCaller connectMaintenanceService(String address, ClientProxyPool proxy) {
        return new MaintenanceJdbcCaller(address, proxy);
    }

    public static MaintenanceJdbcCaller connectMaintenanceService(String host, int port, ClientProxyPool proxy) {
        return new MaintenanceJdbcCaller(host, port, proxy);
    }

    private SwiftMetaData getMetaData(SwiftDatabase schema, String tableName) throws Exception {
        ClientProxy proxy = null;
        try {
            proxy = pool.borrowObject(address);
            return proxy.getProxy(TableService.class).detectiveMetaData(schema, tableName);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            pool.invalidateObject(address, proxy);
            return null;
        } finally {
            pool.returnObject(address, proxy);
        }
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
        ClientProxy proxy = null;
        try {
            proxy = pool.borrowObject(address);
            return proxy.getProxy(TableService.class).detectiveAllTableNames(schema);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            try {
                pool.invalidateObject(address, proxy);
            } catch (Exception ignore) {
            }
            return Collections.emptyList();
        } finally {
            pool.returnObject(address, proxy);
        }
    }

    @Override
    public int createTable(SwiftDatabase schema, String tableName, List<Column> columns) throws Exception {
        ClientProxy proxy = null;
        try {
            proxy = pool.borrowObject(address);
            return proxy.getProxy(TableService.class).createTable(schema, tableName, columns);
        } catch (Exception e) {
            pool.invalidateObject(address, proxy);
            throw new SQLException(e);
        } finally {
            pool.returnObject(address, proxy);
        }
    }

    @Override
    public void dropTable(SwiftDatabase schema, String tableName) throws Exception {
        ClientProxy proxy = null;
        try {
            proxy = pool.borrowObject(address);
            proxy.getProxy(TableService.class).dropTable(schema, tableName);
        } catch (Exception e) {
            pool.invalidateObject(address, proxy);
            throw new SQLException(e);
        } finally {
            pool.returnObject(address, proxy);
        }
    }

    @Override
    public void truncateTable(SwiftDatabase schema, String tableName) throws Exception {
        ClientProxy proxy = null;
        try {
            proxy = pool.borrowObject(address);
            proxy.getProxy(TableService.class).truncateTable(schema, tableName);
        } catch (Exception e) {
            pool.invalidateObject(address, proxy);
            throw new SQLException(e);
        } finally {
            pool.returnObject(address, proxy);
        }
    }

    @Override
    public boolean addColumn(SwiftDatabase schema, String tableName, Column column) throws Exception {
        ClientProxy proxy = null;
        try {
            proxy = pool.borrowObject(address);
            return proxy.getProxy(TableService.class).addColumn(schema, tableName, column);
        } catch (Exception e) {
            pool.invalidateObject(address, proxy);
            throw new SQLException(e);
        } finally {
            pool.returnObject(address, proxy);
        }
    }

    @Override
    public boolean dropColumn(SwiftDatabase schema, String tableName, String columnName) throws Exception {
        ClientProxy proxy = null;
        try {
            proxy = pool.borrowObject(address);
            return proxy.getProxy(TableService.class).dropColumn(schema, tableName, columnName);
        } catch (Exception e) {
            pool.invalidateObject(address, proxy);
            throw new SQLException(e);
        } finally {
            pool.returnObject(address, proxy);
        }
    }

    @Override
    public boolean isTableExists(SwiftDatabase schema, String tableName) {
        return null != detectiveMetaData(schema, tableName);
    }

    public static class SelectJdbcCaller extends JdbcCaller implements SelectService, TableService {
        private SelectJdbcCaller(String address, ClientProxyPool proxy) {
            this.address = address;
            this.pool = proxy;
        }

        private SelectJdbcCaller(String host, int port, ClientProxyPool proxy) {
            this(host + ":" + port, proxy);
        }

        @Override
        public SwiftResultSet query(SwiftDatabase database, String queryJson) throws Exception {
            ClientProxy proxy = null;
            try {
                proxy = pool.borrowObject(address);
                SwiftResultSet resultSet = proxy.getProxy(SelectService.class).query(database, queryJson);
                if (resultSet instanceof SerializableDetailResultSet) {
                    return new SwiftPaginationResultSet((SerializableDetailResultSet) resultSet, this, database);
                }
                return resultSet;
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
                pool.invalidateObject(address, proxy);
                return null;
            } finally {
                pool.returnObject(address, proxy);
            }
        }
    }

    public static class MaintenanceJdbcCaller extends JdbcCaller implements DataMaintenanceService, TableService {
        private MaintenanceJdbcCaller(String address, ClientProxyPool proxy) {
            this.address = address;
            this.pool = proxy;
        }

        private MaintenanceJdbcCaller(String host, int port, ClientProxyPool proxy) {
            this(host + ":" + port, proxy);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws Exception {
            ClientProxy proxy = null;
            try {
                proxy = pool.borrowObject(address);
                return proxy.getProxy(DataMaintenanceService.class).insert(schema, tableName, fields, rows);
            } catch (Exception e) {
                pool.invalidateObject(address, proxy);
                throw new SQLException(e);
            } finally {
                pool.returnObject(address, proxy);
            }
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws Exception {
            return insert(schema, tableName, Collections.<String>emptyList(), rows);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, String queryJson) throws Exception {
            ClientProxy proxy = null;
            try {
                proxy = pool.borrowObject(address);
                return proxy.getProxy(DataMaintenanceService.class).insert(schema, tableName, queryJson);
            } catch (Exception e) {
                pool.invalidateObject(address, proxy);
                throw new SQLException(e);
            } finally {
                pool.returnObject(address, proxy);
            }
        }

        @Override
        public int delete(SwiftDatabase schema, String tableName, Where where) throws Exception {
            ClientProxy proxy = null;
            try {
                proxy = pool.borrowObject(address);
                return proxy.getProxy(DataMaintenanceService.class).delete(schema, tableName, where);
            } catch (Exception e) {
                pool.invalidateObject(address, proxy);
                throw new SQLException(e);
            } finally {
                pool.returnObject(address, proxy);
            }
        }

        @Override
        public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) throws Exception {
            ClientProxy proxy = null;
            try {
                proxy = pool.borrowObject(address);
                return proxy.getProxy(DataMaintenanceService.class).update(schema, tableName, resultSet, where);
            } catch (Exception e) {
                pool.invalidateObject(address, proxy);
                throw new SQLException(e);
            } finally {
                pool.returnObject(address, proxy);
            }
        }
    }
}
