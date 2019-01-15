package com.fr.swift.api.server;

import com.fr.swift.api.info.ApiInvocation;
import com.fr.swift.api.info.ApiRequestParserVisitor;
import com.fr.swift.api.info.ApiRequestType;
import com.fr.swift.api.info.AuthRequestInfo;
import com.fr.swift.api.info.CreateTableRequestInfo;
import com.fr.swift.api.info.DeleteRequestInfo;
import com.fr.swift.api.info.InsertRequestInfo;
import com.fr.swift.api.info.QueryRequestInfo;
import com.fr.swift.api.info.TableRequestInfo;
import com.fr.swift.api.rpc.DataMaintenanceService;
import com.fr.swift.api.rpc.DetectService;
import com.fr.swift.api.rpc.SelectService;
import com.fr.swift.api.rpc.TableService;
import com.fr.swift.api.rpc.bean.Column;
import com.fr.swift.api.server.exception.ApiCrasher;
import com.fr.swift.api.server.response.error.ParamErrorCode;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.impl.SwiftWhere;
import com.fr.swift.jdbc.adaptor.SwiftASTVisitorAdapter;
import com.fr.swift.jdbc.adaptor.SwiftSQLType;
import com.fr.swift.jdbc.adaptor.SwiftSQLUtils;
import com.fr.swift.jdbc.adaptor.bean.ColumnBean;
import com.fr.swift.jdbc.adaptor.bean.CreationBean;
import com.fr.swift.jdbc.adaptor.bean.DeletionBean;
import com.fr.swift.jdbc.adaptor.bean.DropBean;
import com.fr.swift.jdbc.adaptor.bean.InsertionBean;
import com.fr.swift.jdbc.adaptor.bean.SelectionBean;
import com.fr.swift.jdbc.adaptor.bean.TruncateBean;
import com.fr.swift.jdbc.druid.sql.ast.SQLStatement;
import com.fr.swift.jdbc.info.ColumnsRequestInfo;
import com.fr.swift.jdbc.info.JdbcRequestParserVisitor;
import com.fr.swift.jdbc.info.SqlRequestInfo;
import com.fr.swift.jdbc.info.TablesRequestInfo;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.ReflectUtils;
import com.fr.swift.util.Strings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/12/10
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftRequestParserVisitor implements JdbcRequestParserVisitor, ApiRequestParserVisitor {

    private static void setProperties(TableRequestInfo requestInfo, String authCode, String schema, String tableName) {
        requestInfo.setAuthCode(authCode);
        requestInfo.setDatabase(SwiftDatabase.fromKey(schema));
        requestInfo.setTable(tableName);
    }

    @Override
    public ApiInvocation visit(SqlRequestInfo sqlRequestInfo) {
        String sql = sqlRequestInfo.getSql();
        SwiftASTVisitorAdapter visitor = new SwiftASTVisitorAdapter(sqlRequestInfo.getDatabase());
        SQLStatement stmt = SwiftSQLUtils.parseStatement(sql);
        if (stmt == null) {
            return ApiCrasher.crash(ParamErrorCode.SQL_PARSE_ERROR);
        }
        stmt.accept(visitor);
        SwiftSQLType sqlType = visitor.getSqlType();
        String schema = sqlRequestInfo.getDatabase();
        switch (sqlType) {
            case CREATE: {
                CreationBean bean = visitor.getCreationBean();
                CreateTableRequestInfo requestInfo = new CreateTableRequestInfo();
                if (Strings.isNotEmpty(bean.getSchema())) {
                    schema = sqlRequestInfo.getDatabase();
                }
                setProperties(requestInfo, sqlRequestInfo.getAuthCode(), schema, bean.getTableName());
                List<Column> columns = new ArrayList<Column>();
                for (ColumnBean columnBean : bean.getFields()) {
                    columns.add(new Column(columnBean.getColumnName(), columnBean.getColumnType()));
                }
                requestInfo.setColumns(columns);
                return visit(requestInfo);
            }
            case INSERT: {
                InsertionBean bean = visitor.getInsertionBean();
                InsertRequestInfo requestInfo = new InsertRequestInfo();
                if (Strings.isNotEmpty(bean.getSchema())) {
                    schema = bean.getSchema();
                }
                setProperties(requestInfo, sqlRequestInfo.getAuthCode(), schema, bean.getTableName());
                requestInfo.setSelectFields(bean.getFields());
                requestInfo.setData(bean.getRows());
                return visit(requestInfo);
            }
            case DROP: {
                DropBean bean = visitor.getDropBean();
                if (Strings.isNotEmpty(bean.getSchema())) {
                    schema = bean.getSchema();
                }
                TableRequestInfo requestInfo = new TableRequestInfo(ApiRequestType.DROP_TABLE);
                setProperties(requestInfo, sqlRequestInfo.getAuthCode(), schema, bean.getTableName());
                return visit(requestInfo);
            }
            case DELETE: {
                DeletionBean bean = visitor.getDeletionBean();
                DeleteRequestInfo requestInfo = new DeleteRequestInfo();
                if (Strings.isNotEmpty(bean.getSchema())) {
                    schema = bean.getSchema();
                }
                setProperties(requestInfo, sqlRequestInfo.getAuthCode(), schema, bean.getTableName());
                try {
                    requestInfo.setWhere(JsonBuilder.writeJsonString(bean.getFilter()));
                } catch (Exception e) {
                    return ApiCrasher.crash(ParamErrorCode.PARAMS_PARSER_ERROR);
                }
                return visit(requestInfo);
            }
            case SELECT: {
                SelectionBean bean = visitor.getSelectionBean();
                String queryJson = null;
                if (Strings.isNotEmpty(bean.getSchema())) {
                    schema = bean.getSchema();
                }
                try {
                    QueryInfoBean queryInfoBean = bean.getQueryInfoBean();
                    queryInfoBean.setQueryId(sqlRequestInfo.getRequestId());
                    queryJson = JsonBuilder.writeJsonString(queryInfoBean);
                } catch (Exception e) {
                    return ApiCrasher.crash(ParamErrorCode.PARAMS_PARSER_ERROR);
                }
                return createApiInvocation("query", SelectService.class,
                        SwiftDatabase.fromKey(schema), queryJson);
            }
            case TRUNCATE: {
                TruncateBean bean = visitor.getTruncateBean();
                if (Strings.isNotEmpty(bean.getSchema())) {
                    schema = bean.getSchema();
                }
                return createApiInvocation("truncateTable", TableService.class, SwiftDatabase.fromKey(schema), bean.getTableName());
            }
            default:
        }
        return null;
    }

    @Override
    public ApiInvocation visit(ColumnsRequestInfo columnsRequestInfo) {
        String database = columnsRequestInfo.getDatabase();
        String table = columnsRequestInfo.getTable();
        SwiftDatabase swiftDatabase = SwiftDatabase.fromKey(database);
        return createApiInvocation("detectiveMetaData", TableService.class, swiftDatabase, table);
    }

    @Override
    public ApiInvocation visit(TablesRequestInfo tablesRequestInfo) {
        String database = tablesRequestInfo.getDatabase();
        SwiftDatabase swiftDatabase = SwiftDatabase.fromKey(database);
        return createApiInvocation("detectiveAllTable", TableService.class, swiftDatabase);
    }

    private ApiInvocation createApiInvocation(String method, Class<?> clazz, Object... arguments) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method1 : methods) {
            Class<?>[] paramTypes = method1.getParameterTypes();
            if (method1.getName().equals(method) && paramTypes.length == arguments.length) {
                boolean match = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    if (null != arguments[i] && !ReflectUtils.isAssignable(arguments[i].getClass(), paramTypes[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return new ApiInvocation(method, clazz, paramTypes, arguments);
                }
            }
        }
        return Crasher.crash(new NoSuchMethodException());
    }

    @Override
    public ApiInvocation visit(AuthRequestInfo authRequestInfo) {
        return createApiInvocation("detectiveAnalyseAndRealTime", DetectService.class, authRequestInfo.getFrom(), authRequestInfo.getSwiftUser(), authRequestInfo.getSwiftPassword());
    }

    @Override
    public ApiInvocation visit(QueryRequestInfo queryRequestInfo) {
        return createApiInvocation("query", SelectService.class, queryRequestInfo.getDatabase(), queryRequestInfo.getQueryJson());
    }

    @Override
    public ApiInvocation visit(CreateTableRequestInfo createTableRequestInfo) {
        return createApiInvocation("createTable", TableService.class, createTableRequestInfo.getDatabase(),
                createTableRequestInfo.getTable(), createTableRequestInfo.getColumns());
    }

    @Override
    public ApiInvocation visit(DeleteRequestInfo deleteRequestInfo) {
        try {
            String filter = deleteRequestInfo.getWhere();
            SwiftWhere where = new SwiftWhere(JsonBuilder.readValue(filter, FilterInfoBean.class));
            return createApiInvocation("delete", DataMaintenanceService.class, deleteRequestInfo.getDatabase(), deleteRequestInfo.getTable(), where);
        } catch (Exception e) {
            return ApiCrasher.crash(ParamErrorCode.PARAMS_PARSER_ERROR);
        }
    }

    @Override
    public ApiInvocation visit(InsertRequestInfo insertRequestInfo) {
        try {
            return createApiInvocation("insert", DataMaintenanceService.class,
                    insertRequestInfo.getDatabase(),
                    insertRequestInfo.getTable(),
                    insertRequestInfo.getSelectFields(),
                    insertRequestInfo.getData());
        } catch (Exception e) {
            return ApiCrasher.crash(ParamErrorCode.PARAMS_PARSER_ERROR);
        }
    }

    @Override
    public ApiInvocation visit(TableRequestInfo tableRequestInfo) {
        ApiRequestType type = tableRequestInfo.getRequest();
        switch (type) {
            case DROP_TABLE:
                return createApiInvocation("dropTable", TableService.class, tableRequestInfo.getDatabase(), tableRequestInfo.getTable());
            case TRUNCATE_TABLE:
                return createApiInvocation("truncateTable", TableService.class, tableRequestInfo.getDatabase(), tableRequestInfo.getTable());
            case DELETE:
                return visit((DeleteRequestInfo) tableRequestInfo);
            case INSERT:
                return visit((InsertRequestInfo) tableRequestInfo);
            case CREATE_TABLE:
                return visit((CreateTableRequestInfo) tableRequestInfo);
            default:

        }
        return null;
    }
}
