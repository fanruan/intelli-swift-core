package com.fr.swift.api.rpc.session.impl;

import com.fr.swift.api.info.ApiRequestType;
import com.fr.swift.api.info.CreateTableRequestInfo;
import com.fr.swift.api.info.DeleteTableRequestInfo;
import com.fr.swift.api.info.InsertRequestInfo;
import com.fr.swift.api.info.QueryRequestInfo;
import com.fr.swift.api.info.TableRequestInfo;
import com.fr.swift.api.result.SwiftApiResultSet;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.api.rpc.session.SwiftPublicApiSession;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Where;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.serialize.SerializableDetailResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiSessionImpl implements SwiftPublicApiSession {

    private String analyseAddress;
    private String realTimeAddress;
    private SwiftApiSessionFactoryImpl sessionFactory;
    private ObjectMapper objectMapper = new ObjectMapper();


    public SwiftApiSessionImpl(String analyseAddress, String realTimeAddress, SwiftApiSessionFactoryImpl sessionFactory) {
        this.analyseAddress = analyseAddress;
        this.realTimeAddress = realTimeAddress;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, List<String> fields, List<Row> rows) throws Exception {
        InsertRequestInfo info = new InsertRequestInfo();
        info.setData(rows);
        info.setDatabase(schema);
        info.setTable(tableName);
        info.setAuthCode(sessionFactory.getAuthCode());
        info.setSelectFields(fields);
        ApiResponse response = sessionFactory.callRpc(realTimeAddress, info);
        if (response.isError()) {
            throw new Exception(response.description());
        }
        return (Integer) response.result();
    }

    @Override
    public int insert(SwiftDatabase schema, String tableName, List<Row> rows) throws Exception {
        return insert(schema, tableName, Collections.<String>emptyList(), rows);
    }

    @Override
    public int delete(SwiftDatabase schema, String tableName, Where where) throws Exception {
        DeleteTableRequestInfo info = new DeleteTableRequestInfo();
        info.setDatabase(schema);
        info.setTable(tableName);
        info.setAuthCode(sessionFactory.getAuthCode());
        info.setWhere(objectMapper.writeValueAsString(where.getFilterBean()));
        ApiResponse response = sessionFactory.callRpc(realTimeAddress, info);
        if (response.isError()) {
            throw new Exception(response.description());
        }
        return (Integer) response.result();
    }

    @Override
    public SwiftResultSet query(SwiftDatabase database, String queryJson) throws Exception {
        QueryRequestInfo info = new QueryRequestInfo();
        info.setAuthCode(sessionFactory.getAuthCode());
        info.setQueryJson(queryJson);
        ApiResponse response = sessionFactory.callRpc(realTimeAddress, info);
        if (response.isError()) {
            throw new Exception(response.description());
        }
        SwiftResultSet result = (SwiftResultSet) response.result();
        if (result instanceof SerializableDetailResultSet) {
            return new SwiftApiResultSet((SerializableDetailResultSet) result, database, this);
        }
        return result;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean createTable(SwiftDatabase schema, String tableName, List<Column> columns) throws Exception {
        CreateTableRequestInfo info = new CreateTableRequestInfo();
        info.setDatabase(schema);
        info.setAuthCode(sessionFactory.getAuthCode());
        info.setColumns(columns);
        info.setTable(tableName);
        ApiResponse response = sessionFactory.callRpc(realTimeAddress, info);
        if (response.isError()) {
            throw new Exception(response.description());
        }
        return (Boolean) response.result();
    }

    @Override
    public void dropTable(SwiftDatabase schema, String tableName) throws Exception {
        TableRequestInfo info = new TableRequestInfo(ApiRequestType.DROP_TABLE);
        info.setDatabase(schema);
        info.setTable(tableName);
        info.setAuthCode(sessionFactory.getAuthCode());
        ApiResponse response = sessionFactory.callRpc(realTimeAddress, info);
        if (response.isError()) {
            throw new Exception(response.description());
        }
    }

    @Override
    public void truncateTable(SwiftDatabase schema, String tableName) throws Exception {
        TableRequestInfo info = new TableRequestInfo(ApiRequestType.TRUNCATE_TABLE);
        info.setDatabase(schema);
        info.setTable(tableName);
        info.setAuthCode(sessionFactory.getAuthCode());
        ApiResponse response = sessionFactory.callRpc(realTimeAddress, info);
        if (response.isError()) {
            throw new Exception(response.description());
        }
    }

    @Override
    public SwiftResultSet query(SwiftDatabase database, QueryBean queryBean) throws Exception {
        return query(database, queryBean.toString());
    }
}
