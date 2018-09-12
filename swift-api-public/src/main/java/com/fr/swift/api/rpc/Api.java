package com.fr.swift.api.rpc;

import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.api.rpc.invoke.CallClient;
import com.fr.swift.api.rpc.pool.CallClientPool;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.exception.meta.SwiftMetaDataAbsentException;
import com.fr.swift.rpc.bean.RpcResponse;
import com.fr.swift.rpc.bean.impl.RpcRequest;
import com.fr.swift.service.ServiceType;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    static DetectService connectDetectiveApi(String address) {
        return new DetectiveApi(address, DEFAULT_MAX_FRAME_SIZE);
    }

    static DetectService connectDetectiveApi(String address, int maxFrameSize) {
        return new DetectiveApi(address, maxFrameSize);
    }

    @Override
    public SwiftMetaData detectiveMetaData(SwiftDatabase schema, String tableName) throws SwiftMetaDataAbsentException {
        TableService service = getProxy(TableService.class);
        return service.detectiveMetaData(schema, tableName);
    }

    @Override
    public List<String> detectiveAllTableNames(SwiftDatabase schema) {
        TableService service = getProxy(TableService.class);
        return service.detectiveAllTableNames(schema);
    }

    @Override
    public boolean isTableExists(SwiftDatabase schema, String tableName) {
        TableService service = getProxy(TableService.class);
        return service.isTableExists(schema, tableName);
    }

    @Override
    public int createTable(SwiftDatabase schema, String tableName, List<Column> columns) throws Exception {
        TableService service = getProxy(TableService.class);
        return service.createTable(schema, tableName, columns);
    }

    @Override
    public boolean addColumn(SwiftDatabase schema, String tableName, Column column) throws Exception {
        TableService service = getProxy(TableService.class);
        return service.addColumn(schema, tableName, column);
    }

    @Override
    public boolean dropColumn(SwiftDatabase schema, String tableName, String columnName) throws Exception {
        TableService service = getProxy(TableService.class);
        return service.dropColumn(schema, tableName, columnName);
    }

    protected <T> T getProxy(final Class<T> proxyClass) {
        return (T) Proxy.newProxyInstance(proxyClass.getClassLoader(), new Class[]{proxyClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setInterfaceName(proxyClass.getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                CallClient client = CallClientPool.getInstance(maxFrameSize).borrowObject(address);
                if (!client.isActive()) {
                    CallClientPool.getInstance(maxFrameSize).returnObject(address, client);
                    CallClientPool.getInstance(maxFrameSize).invalidateObject(address, client);
                    client = CallClientPool.getInstance(maxFrameSize).borrowObject(address);
                }
                try {
                    RpcResponse response = client.send(request);
                    if (null != response.getException()) {
                        throw response.getException();
                    }
                    return response.getResult();
                } finally {
                    CallClientPool.getInstance(maxFrameSize).returnObject(address, client);
                }
            }
        });
    }

    static class DetectiveApi extends Api implements DetectService {

        private DetectiveApi(String address, int maxFrameSize) {
            super(address, maxFrameSize);
        }

        @Override
        public Map<ServiceType, List<String>> detectiveAnalyseAndRealTime(String defaultAddress) {
            return getProxy(DetectService.class).detectiveAnalyseAndRealTime(defaultAddress);
        }
    }

    public static class SelectApi extends Api implements SelectService {

        private SelectApi(String address, int maxFrameSize) {
            super(address, maxFrameSize);
        }

        @Override
        public SwiftResultSet query(SwiftDatabase database, String queryJson) throws Exception {
            SelectService service = getProxy(SelectService.class);
            return service.query(database, queryJson);
        }
    }

    public static class DataMaintenanceApi extends Api implements DataMaintenanceService {

        private DataMaintenanceApi(String address, int maxFrameSize) {
            super(address, maxFrameSize);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws Exception {
            DataMaintenanceService service = getProxy(DataMaintenanceService.class);
            return service.insert(schema, tableName, fields, rows);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws Exception {
            return insert(schema, tableName, Collections.<String>emptyList(), rows);
        }

        @Override
        public int insert(SwiftDatabase schema, String tableName, String queryJson) throws Exception {
            DataMaintenanceService service = getProxy(DataMaintenanceService.class);
            return service.insert(schema, tableName, queryJson);
        }

        @Override
        public int delete(SwiftDatabase schema, String tableName, Where where) throws Exception {
            DataMaintenanceService service = getProxy(DataMaintenanceService.class);
            return service.delete(schema, tableName, where);
        }

        @Override
        public int update(SwiftDatabase schema, String tableName, SwiftResultSet resultSet, Where where) throws Exception {
            DataMaintenanceService service = getProxy(DataMaintenanceService.class);
            return service.update(schema, tableName, resultSet, where);
        }
    }
}
